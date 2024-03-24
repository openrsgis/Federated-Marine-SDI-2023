package whu.edu.cn.geostreamcube.scala.entity

import geotrellis.layer.SpatialKey
import geotrellis.raster.Tile

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MeteorologyTilePro extends Serializable {
  var productName: String = _
  var variableName: String = _
  var extent: geotrellis.vector.Extent = _
  var time: LocalDateTime = _
  var spatialKey: SpatialKey = _
  var tile: Tile = _
  var zoom: Int = _
  var zCurveIndex: Int = _

  def getProductName: String = {
    this.productName
  }

  def setProductName(productName: String): Unit = {
    this.productName = productName
  }

  def getVariableName: String = {
    this.variableName
  }

  def setVariableName(variableName: String): Unit = {
    this.variableName = variableName
  }

  def getExtent: geotrellis.vector.Extent = {
    this.extent
  }

  def setExtent(extent: geotrellis.vector.Extent): Unit = {
    this.extent = extent
  }

  def getTime: LocalDateTime = {
    this.time
  }

  def setTime(time: String): Unit = {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    val localDateTime: LocalDateTime = LocalDateTime.parse(time, formatter)
    this.time = localDateTime
  }

  def getSpatialKey: SpatialKey = {
    this.spatialKey
  }

  def setSpatialKey(spatialKey: SpatialKey): Unit = {
    this.spatialKey = spatialKey
  }

  def getTile: Tile = {
    this.tile
  }

  def setTile(tile: Tile): Unit = {
    this.tile = tile
  }

  def getZoom: Int = {
    this.zoom
  }

  def setZoom(zoom: Int): Unit = {
    this.zoom = zoom
  }

  def getZCurveIndex: Int = {
    this.zCurveIndex
  }

  def setZCurveIndex(zCurveIndex: Int): Unit = {
    this.zCurveIndex = zCurveIndex
  }


}
