import { fromUrl as tiffFromUrl } from 'geotiff'
const lodash = require('lodash')
const { sortedIndex } = lodash

export class TemporalWaveHeightDataset {
  minTime = 0
  maxTime = 0
  timeSequence = []
  latSpan = 0
  lngSpan = 0

  constructor(bands, extent, width, height, noDataValue) {
    this.bands = bands
    this.extent = extent
    this.width = width
    this.height = height
    this.noDataValue = noDataValue
    this.maxTime = bands[bands.length - 1].time
    this.minTime = bands[0].time
    this.timeSequence = bands.map(e => e.time)
    this.latSpan = extent.north - extent.south
    this.lngSpan = extent.east - extent.west

    console.log(`Constructing TemporalWaveHeightDataset with
        ${bands.length} bands
        time range: ${new Date(this.minTime).toISOString()} - ${new Date(this.maxTime).toISOString()}
        extent: west:${extent.west} east:${extent.east} south:${extent.south} north:${extent.north}
        size: width:${width} height:${height}
        `)
  }

  getDescription() {
    return `${this.bands.length} temporal bands;
    time range: ${new Date(this.minTime).toISOString()} - ${new Date(this.maxTime).toISOString()};
    extent: west:${this.extent.west} east:${this.extent.east} south:${this.extent.south} north:${this.extent.north};
    size: width:${this.width} height:${this.height};`
  }

  getNoData() {
    return this.noDataValue
  }

  getSampler(time, options) {
    const {
      bands,
      minTime,
      maxTime,
      timeSequence,
      width,
      height,
      latSpan,
      lngSpan,
      noDataValue,
      extent: { east: maxLng, north: maxLat, south: minLat, west: minLng }
    } = this

    const { method } = options

    if (time < minTime || time > maxTime) {
      throw new Error(`sampling timestamp out of range: [${minTime}, ${maxTime}]`)
    }

    let forwardIndex = -1,
      backwardIndex = -1

    if (time === minTime) {
      forwardIndex = backwardIndex = 0
    } else if (time === maxTime) {
      forwardIndex = backwardIndex = bands.length - 1
    } else {
      forwardIndex = sortedIndex(timeSequence, time)
      backwardIndex = forwardIndex - 1
    }

    const { bandData: forwardBandData, time: forwardBandTime } = bands[forwardIndex]
    const { bandData: backwardBandData, time: backwardBandTime } = bands[backwardIndex]
    const timeDiff = forwardBandTime - backwardBandTime

    const bilinearInterp = (band, x, y) => {
      const x1 = Math.floor(x),
        y1 = Math.floor(y)
      const x2 = x1 + 1,
        y2 = y1 + 1

      const p11 = band[y1 * width + x1]
      let p12 = band[y2 * width + x1]
      if (p12 === undefined) p12 = p11
      let p21 = band[y1 * width + x2]
      if (p21 === undefined) p21 = p11
      let p22 = band[y2 * width + x2]
      if (p22 === undefined) p22 = p11

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

      const forwardPixelValue = interpolate(forwardBandData, x, y)
      const backwardPixelValue = interpolate(backwardBandData, x, y)

      // console.log(
      //     `lnglat(${lng},${lat}) xy:(${x},${y}) val:(${backwardPixelValue},${forwardPixelValue})`
      // );

      return ((time - backwardBandTime) / timeDiff) * (forwardPixelValue - backwardPixelValue) + backwardPixelValue
    }
  }

  getExtent() {
    return this.extent
  }

  getTimeSpan() {
    return {
      start: this.minTime,
      end: this.maxTime
    }
  }

  static async fromUrl(baseUrl) {
    const { files, extent, NO_DATA } = await (await fetch(`${baseUrl}/index.json`)).json()

    const bands = []

    let lastTime = 0
    let seqWidth = null,
      seqHeight = null

    for (const { name, time } of files) {
      const timestamp = new Date(time).getTime()

      if (timestamp <= lastTime) {
        throw new Error(
          `Invalid sequence of bands: error reading tiff file ${name}(${time}) timestamp must be in ascending order.`
        )
      }

      lastTime = timestamp

      const tiff = await tiffFromUrl(`${baseUrl}/${name}`)
      const tiffBand = await tiff.getImage()
      const height = tiffBand.getHeight()
      const width = tiffBand.getWidth()

      if (seqHeight === null) {
        seqHeight = height
        seqWidth = width
      }
      if (seqHeight !== height || seqWidth !== width) {
        throw new Error('Invalid sequence of bands: inconsistent size value.')
      }

      const bandData = await tiff.readRasters({
        interleave: true,
        samples: [0]
      })

      bands.push({
        bandData: bandData,
        time: timestamp
      })
    }

    if (bands.length < 2) {
      throw new Error('Not enough bands.')
    }

    return new TemporalWaveHeightDataset(bands, extent, seqWidth, seqHeight, NO_DATA)
  }
}

