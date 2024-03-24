const Cesium = require('cesium/Cesium')

export function getColorRamp(values) {
  const ramp = document.createElement('canvas')
  ramp.width = 100
  ramp.height = 1
  const ctx = ramp.getContext('2d')

  const grd = ctx.createLinearGradient(0, 0, 100, 0)

  for (const v of values) {
    grd.addColorStop(v.left / 100.0, toHex(v.red, v.green, v.blue))
  }

  ctx.fillStyle = grd
  ctx.fillRect(0, 0, 100, 1)

  return ramp
}

function toHex(r, g, b) {
  return '#' + r.toString(16).padStart(2, '0') + g.toString(16).padStart(2, '0') + b.toString(16).padStart(2, '0')
}
