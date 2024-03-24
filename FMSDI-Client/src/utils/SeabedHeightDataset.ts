import { fromUrl as tiffFromUrl } from 'geotiff'
import * as Cesium from 'cesium'

type SeabedImageBand = Float64Array

type LatLngExtent = {
  west: number
  east: number
  south: number
  north: number
}

export class SeabedHeightDataset {
  private latSpan: number = 0
  private lngSpan: number = 0

  private constructor(
    private bandData: SeabedImageBand,
    private extent: LatLngExtent,
    private width: number,
    private height: number,
    private noDataValue: number
  ) {
    this.latSpan = extent.north - extent.south
    this.lngSpan = extent.east - extent.west

    console.log(`Constructing SeabedHeightDataset
        extent: west:${extent.west} east:${extent.east} south:${extent.south} north:${extent.north}
        size: width:${width} height:${height}
        `)
  }

  getNoData() {
    return this.noDataValue
  }

  getSampler(options: { method: 'bilinear' | 'nearest' }): (lng: number, lat: number) => number {
    const {
      width,
      height,
      latSpan,
      lngSpan,
      noDataValue,
      extent: { east: maxLng, north: maxLat, south: minLat, west: minLng }
    } = this

    const { method } = options

    const band = this.bandData

    const bilinearInterp = (band: SeabedImageBand, x: number, y: number) => {
      const x1 = Math.floor(x),
        y1 = Math.floor(y)
      const x2 = x1 + 1,
        y2 = y1 + 1

      const p11 = band[y1 * width + x1]
      const p12 = band[y2 * width + x1] ?? p11
      const p21 = band[y1 * width + x2] ?? p11
      const p22 = band[y2 * width + x2] ?? p11

      // console.log(
      //     `(${x},${y},${x1},${x2},${y1},${y2})p11:${p11},p12:${p12},p21:${p21},p22:${p22}`
      // );

      if (p11 === noDataValue || p12 === noDataValue || p21 === noDataValue || p22 === noDataValue) {
        return noDataValue
      }

      return (
        p11 * (x2 - x) * (y2 - y) + p21 * (x - x1) * (y2 - y) + p12 * (x2 - x) * (y - y1) + p22 * (x - x1) * (y - y1)
      )
    }

    const nearestInterp = (band: SeabedImageBand, x: number, y: number) => {
      return band[Math.floor(y) * width + Math.floor(x)]
    }

    let interpolate: (band: SeabedImageBand, x: number, y: number) => number = null

    switch (method) {
      case 'bilinear':
        interpolate = bilinearInterp
        break
      case 'nearest':
        interpolate = nearestInterp
        break
    }

    return (lng, lat) => {
      // console.log(`lnglat(${lng},${lat})`);
      if (lng < minLng || lng > maxLng || lat < minLat || lat > maxLat) {
        return noDataValue
      }

      const x = ((lng - minLng) / lngSpan) * (width - 1)
      const y = ((maxLat - lat) / latSpan) * (height - 1)

      const pixelVal = interpolate(band, x, y)

      // console.log(`lnglat(${lng},${lat}) xy:(${x},${y}) val:${pixelVal}`);

      return pixelVal
    }
  }

  getExtent(): LatLngExtent {
    return this.extent
  }

  getElevationBandMaterialAppearance(): Cesium.EllipsoidSurfaceAppearance {
    return new Cesium.EllipsoidSurfaceAppearance({
      material: new Cesium.Material({
        fabric: {
          source: `
                    czm_material czm_getMaterial(czm_materialInput materialInput)
                    {
                        czm_material m = czm_getDefaultMaterial(materialInput);
                        m.diffuse = vec3(0.5);
                        m.specular = 0.5;
                        return m;
                    }
                    `
        }
      }),
      vertexShaderSource: `
            in vec4 color;

            in vec3 position3DHigh;
            in vec3 position3DLow;
            in float batchId;
            in vec2 st;

            out vec2 v_st;
            out vec3 v_positionEC;
            out vec4 v_color;

            void main() {
                v_st = st;
                vec4 p = czm_computePosition();
                v_positionEC = (czm_modelViewRelativeToEye * p).xyz;      // position in eye coordinates
                gl_Position = czm_modelViewProjectionRelativeToEye * p;
                v_color = color;
            }

            `,
      fragmentShaderSource: `
            in vec2 v_st;
            in vec3 v_positionEC;
            in vec4 v_color;

            void main() {
                out_FragColor = v_color;   
            }
            `
    })
  }

  static async fromUrl(url: string, extent: LatLngExtent) {
    let seqWidth: number = null,
      seqHeight: number = null

    const tiff = await tiffFromUrl(url)
    const tiffBand = await tiff.getImage(0)
    const height = tiffBand.getHeight()
    const width = tiffBand.getWidth()
    const noDataVal = tiffBand.getGDALNoData()

    if (seqHeight === null) {
      seqHeight = height
      seqWidth = width
    }
    if (seqHeight !== height || seqWidth !== width) {
      throw new Error('Invalid sequence of bands: inconsistent size value.')
    }

    const bandData = (await tiff.readRasters({
      interleave: true,
      samples: [0]
    })) as SeabedImageBand

    return new SeabedHeightDataset(bandData, extent, seqWidth, seqHeight, noDataVal)
  }
}
