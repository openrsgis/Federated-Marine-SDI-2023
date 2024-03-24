const Cesium = require('cesium/Cesium')
export function computeTerrainGeometry(
  rect,
  sampler,
  options = {
    latSamples: 10,
    lngSamples: 10,
    NO_DATA_VALUE: -Number.MAX_VALUE,
    exaggerationMultiplier: 1.0,
    replaceNoData: 0,
    colorSteps: []
  },
  ellipsoid = Cesium.Ellipsoid.WGS84
) {
  const [west, east, north, south] = rad2Deg(rect.west, rect.east, rect.north, rect.south)
  let { latSamples, lngSamples, colorSteps, NO_DATA_VALUE, replaceNoData, exaggerationMultiplier } = options
  if (!colorSteps) colorSteps = []
  const computedColorSteps = colorSteps.map(({ color, height }) => ({
    color: [color[0] / 255.0, color[1] / 255.0, color[2] / 255.0, color[3]],
    height
  }))
  if (lngSamples < 2 || latSamples < 2) throw new Error('Invalid sampling parameter.')
  // validate height sequence
  if (
    colorSteps.length >= 1 &&
    colorSteps
      .map(v => v.height)
      .reduce((prev, cur, idx) => {
        if (prev === -1.0) return -1.0
        return cur > colorSteps[idx - 1].height ? 1.0 : -1.0
      }) === -1.0
  ) {
    throw new Error('Color steps must be arranged in height ascending order.')
  }
  const latSpan = north - south
  const lngSpan = east - west
  // Vertex attributes
  const positions = new Float64Array(latSamples * lngSamples * 3).fill(0.0)
  const colors = new Float32Array(latSamples * lngSamples * 4).fill(0.0)
  // Element indices(for TRIANGLES)
  const indices = new Uint16Array((latSamples - 1) * (lngSamples - 1) * 6).fill(0)
  // Texture coordinates
  const sts = new Float64Array(latSamples * lngSamples * 2).fill(0.0)
  let vertexIdx = 0,
    posIdx = 0,
    colorIdx = 0,
    elemIdx = 0,
    stIdx = 0
  function interpolateColor(height) {
    if (computedColorSteps.length === 0) return [0.0, 0.0, 0.0, 0.0]
    if (height <= computedColorSteps[0].height) return computedColorSteps[0].color
    if (height >= computedColorSteps[computedColorSteps.length - 1].height)
      return computedColorSteps[computedColorSteps.length - 1].color
    let i = 1
    while (i < computedColorSteps.length) {
      if (height < computedColorSteps[i].height) {
        const { color: rclr, height: rhgt } = computedColorSteps[i]
        const { color: lclr, height: lhgt } = computedColorSteps[i - 1]
        const hgtScale = (height - lhgt) / (rhgt - lhgt)
        return Array(4)
          .fill(0.0)
          .map((_, idx) => hgtScale * (rclr[idx] - lclr[idx]) + lclr[idx])
      }
      ++i
    }
  }
  for (let gridY = 0; gridY < latSamples; gridY++) {
    for (let gridX = 0; gridX < lngSamples; gridX++) {
      const lat = south + (latSpan / (latSamples - 1)) * gridY
      const lng = west + (lngSpan / (lngSamples - 1)) * gridX
      const height = sampler(lng, lat)
      const replacedHeight = height === NO_DATA_VALUE && replaceNoData !== false ? replaceNoData : height - 3.0
      const exaggeratedHeight = replacedHeight * exaggerationMultiplier
      // console.log(`height at (${lng},${lat}): ${height}>${replacedHeight}`)
      const verCoord = Cesium.Cartesian3.fromDegrees(lng, lat, exaggeratedHeight, ellipsoid)
      positions[posIdx++] = verCoord.x
      positions[posIdx++] = verCoord.y
      positions[posIdx++] = verCoord.z
      sts[stIdx++] = (1.0 / (latSamples - 1)) * gridY
      sts[stIdx++] = (1.0 / (lngSamples - 1)) * gridX
      const color = interpolateColor(exaggeratedHeight)
      // console.log(color)
      colors[colorIdx++] = color[0]
      colors[colorIdx++] = color[1]
      colors[colorIdx++] = color[2]
      colors[colorIdx++] = color[3]
      if (gridX * gridY !== 0) {
        indices[elemIdx++] = vertexIdx
        indices[elemIdx++] = vertexIdx - 1
        indices[elemIdx++] = vertexIdx - lngSamples - 1
        indices[elemIdx++] = vertexIdx
        indices[elemIdx++] = vertexIdx - lngSamples - 1
        indices[elemIdx++] = vertexIdx - lngSamples
      }
      vertexIdx++
    }
  }
  const geomAttributes = new Cesium.GeometryAttributes()
  geomAttributes.position = new Cesium.GeometryAttribute({
    componentDatatype: Cesium.ComponentDatatype.DOUBLE,
    componentsPerAttribute: 3,
    normalize: false,
    values: positions
  })
  geomAttributes.color = new Cesium.GeometryAttribute({
    componentDatatype: Cesium.ComponentDatatype.FLOAT,
    componentsPerAttribute: 4,
    normalize: false,
    values: colors
  })
  geomAttributes.st = new Cesium.GeometryAttribute({
    componentDatatype: Cesium.ComponentDatatype.FLOAT,
    componentsPerAttribute: 2,
    normalize: false,
    values: sts
  })
  const geom = new Cesium.Geometry({
    attributes: geomAttributes,
    indices,
    boundingSphere: Cesium.BoundingSphere.fromVertices(Array.from(positions)),
    primitiveType: Cesium.PrimitiveType.TRIANGLES
  })
  return geom
}
function rad2Deg(...rads) {
  return rads.map(r => Cesium.Math.toDegrees(r))
}

