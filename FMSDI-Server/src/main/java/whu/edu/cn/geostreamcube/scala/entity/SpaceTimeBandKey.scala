package whu.edu.cn.geostreamcube.scala.entity

import geotrellis.layer.SpaceTimeKey

import scala.beans.BeanProperty

/**
 * Extend Geotrellis SpaceTimeKey to contain measurement dimension.
 *
 */
case class SpaceTimeBandKey(_spaceTimeKey: SpaceTimeKey, _measurementName: String){
  @BeanProperty
  var spaceTimeKey = _spaceTimeKey
  @BeanProperty
  var measurementName = _measurementName
}
