var __awaiter =
  (this && this.__awaiter) ||
  function(thisArg, _arguments, P, generator) {
    function adopt(value) {
      return value instanceof P
        ? value
        : new P(function(resolve) {
            resolve(value)
          })
    }
    return new (P || (P = Promise))(function(resolve, reject) {
      function fulfilled(value) {
        try {
          step(generator.next(value))
        } catch (e) {
          reject(e)
        }
      }
      function rejected(value) {
        try {
          step(generator['throw'](value))
        } catch (e) {
          reject(e)
        }
      }
      function step(result) {
        result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected)
      }
      step((generator = generator.apply(thisArg, _arguments || [])).next())
    })
  }
import { fromUrl as tiffFromUrl } from 'geotiff'
const Cesium = require('cesium/Cesium')
export class SeabedHeightDataset {
  constructor(bandData, extent, width, height, noDataValue) {
    this.bandData = bandData
    this.extent = extent
    this.width = width
    this.height = height
    this.noDataValue = noDataValue
    this.latSpan = 0
    this.lngSpan = 0
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
  getSampler(options) {
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
    const bilinearInterp = (band, x, y) => {
      var _a, _b, _c
      const x1 = Math.floor(x),
        y1 = Math.floor(y)
      const x2 = x1 + 1,
        y2 = y1 + 1
      const p11 = band[y1 * width + x1]
      const p12 = (_a = band[y2 * width + x1]) !== null && _a !== void 0 ? _a : p11
      const p21 = (_b = band[y1 * width + x2]) !== null && _b !== void 0 ? _b : p11
      const p22 = (_c = band[y2 * width + x2]) !== null && _c !== void 0 ? _c : p11
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
    const nearestInterp = (band, x, y) => {
      return band[Math.floor(y) * width + Math.floor(x)]
    }
    let interpolate = null
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
  getExtent() {
    return this.extent
  }
  getElevationBandMaterialAppearance() {
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
            attribute vec4 color_0;
            attribute float batchId;
            attribute vec2 st;
            attribute vec3 normal;
            attribute vec3 position3DHigh;
            attribute vec3 position3DLow;
            varying vec2 v_st;
            varying vec3 v_positionEC;
            varying vec4 v_color;

            void main() {
                v_st = st;
                vec4 p = czm_computePosition();
                v_positionEC = (czm_modelViewRelativeToEye * p).xyz;      // position in eye coordinates
                gl_Position = czm_modelViewProjectionRelativeToEye * p;
                v_color = color_0;
            }

            `,
      fragmentShaderSource: `
            varying vec2 v_st;
            varying vec3 v_positionEC;
            varying vec4 v_color;

            void main() {
                gl_FragColor = v_color;   
            }
            `
    })
  }
  static fromUrl(url, extent) {
    return __awaiter(this, void 0, void 0, function*() {
      let seqWidth = null,
        seqHeight = null
      const tiff = yield tiffFromUrl(url)
      const tiffBand = yield tiff.getImage(0)
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
      const bandData = yield tiff.readRasters({
        interleave: true,
        samples: [0]
      })
      return new SeabedHeightDataset(bandData, extent, seqWidth, seqHeight, noDataVal)
    })
  }
}

