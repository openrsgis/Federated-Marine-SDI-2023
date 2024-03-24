export function parseGradientMatrix(m) {
  return m.map(v => ({
    left: v[0],
    red: v[1],
    green: v[2],
    blue: v[3],
    alpha: v[4]
  }))
}
