package whu.edu.cn.geostreamcube.scala.service

import whu.edu.cn.geostreamcube.scala.util.PostgresqlUtil
import whu.edu.cn.geostreamcube.scala.util.WebTileUtil.judgeWebTile

import scala.collection.mutable
import java.sql.{Connection, ResultSet, Statement}
import scala.util.Random

object PostgresqlService {

  /**
   * Determining whether the web tiles need to be computed
   *
   * @param taskID      the taskID generated by the web
   * @param level       the level of the web window
   * @param windowRange the spatial extent of the web window
   * @param startTime
   * @param endTime
   * @return Precise spatial extent for query
   */
  def getMeteorologyTileFact(taskID: String, level: Int, windowRange: String, startTime: String, endTime: String, product: String, variable: String): Unit = {
    val extent: String = judgeWebTile(taskID, level, windowRange)
    if (extent == null) {
      throw new IllegalArgumentException("没有瓦片需要计算")
    }
    val postgresqlUtil = new PostgresqlUtil("")
    val conn: Connection = postgresqlUtil.getConnection
    if (conn != null) {
      try {
        // Configure to be Read Only
        val statement: Statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)

        // Extent Level
        val extentLevelSql = new mutable.StringBuilder
        extentLevelSql ++= "select extentlevel_key from extentLevel where level = "
        extentLevelSql ++= level.toString
        extentLevelSql ++= " and globe = 1 and crs = 'EPSG:4326'"
        println(extentLevelSql)
        val extentLevelResults: ResultSet = statement.executeQuery(extentLevelSql.toString())
        if (!extentLevelResults.first()) {
          throw new IllegalArgumentException("不存在当前层级")
        }
        // Extent dimension
        val extentSql = new mutable.StringBuilder
        extentSql ++= "select extent_key from extent where extentlevel_key = "
        extentSql ++= extentLevelResults.getString("extentlevel_key")
        extentSql ++= " and st_intersects(geom, st_geomfromtext('" + extent + "',4326))"
        println(extentSql)
        val extentResults: ResultSet = statement.executeQuery(extentSql.toString())

        val extentKeys = new StringBuilder
        if (extentResults.first()) {
          extentResults.previous()
          extentKeys ++= "("
          while (extentResults.next) {
            extentKeys ++= "\'"
            extentKeys ++= extentResults.getString("extent_key")
            extentKeys ++= "\',"
          }
          extentKeys.deleteCharAt(extentKeys.length - 1)
          extentKeys ++= ")"
        }
        println("extent keys are " + extentKeys)
        // Time dimension
        val timeSql = new mutable.StringBuilder
        val timeLevelKey = 1
        timeSql ++= "select time_key from time where timelevel_key = "
        timeSql ++= timeLevelKey.toString
        timeSql ++= " and timestamp >= '" + startTime + "' and timestamp <= '" + endTime + "'"
        println(timeSql)
        val timeResults: ResultSet = statement.executeQuery(timeSql.toString())
        val timeKeys = new StringBuilder
        if (timeResults.first()) {
          timeResults.previous()
          timeKeys ++= "("
          while (timeResults.next) {
            timeKeys ++= "\'"
            timeKeys ++= timeResults.getString("time_key")
            timeKeys ++= "\',"
          }
          timeKeys.deleteCharAt(timeKeys.length - 1)
          timeKeys ++= ")"
        }
        println("time keys are " + timeKeys)
        // Product dimension
        val productSql = new mutable.StringBuilder
        productSql ++= "select product_key from product where product_name = '"
        productSql ++= product + "'"
        println(productSql)
        val productResults: ResultSet = statement.executeQuery(productSql.toString())
        val productKeys = new StringBuilder
        if (productResults.first()) {
          productResults.previous()
          productKeys ++= "("
          while (productResults.next) {
            productKeys ++= "\'"
            productKeys ++= productResults.getString("product_key")
            productKeys ++= "\',"
          }
          productKeys.deleteCharAt(productKeys.length - 1)
          productKeys ++= ")"
        }
        println("product keys are " + productKeys)
        // Variable dimension
        val variableSql = new mutable.StringBuilder
        variableSql ++= "select variable_key from variable where variable_name = '"
        variableSql ++= variable + "'"
        println(variableSql)
        val variableResults: ResultSet = statement.executeQuery(variableSql.toString())
        val variableKeys = new StringBuilder
        if (variableResults.first()) {
          variableResults.previous()
          variableKeys ++= "("
          while (variableResults.next) {
            variableKeys ++= "\'"
            variableKeys ++= variableResults.getString("variable_key")
            variableKeys ++= "\',"
          }
          variableKeys.deleteCharAt(variableKeys.length - 1)
          variableKeys ++= ")"
        }
        println("variable keys are " + variableKeys)
        // Tile
        val tileSql = new mutable.StringBuilder
        tileSql ++= "select id from meteorology_tile_fact where "
        if (extentKeys.nonEmpty) {
          tileSql ++= "extent_key in " + extentKeys
          tileSql ++= " and "
        }
        else {
          throw new IllegalArgumentException("选择范围内没有符合条件的瓦片")
        }
        if (timeKeys.nonEmpty) {
          tileSql ++= "time_key in " + timeKeys
          tileSql ++= " and "
        }
        else {
          throw new IllegalArgumentException("选择时间内没有符合条件的瓦片")
        }
        if (productKeys.nonEmpty) {
          tileSql ++= "product_key in " + productKeys
          tileSql ++= " and "
        }
        else {
          throw new IllegalArgumentException("选择产品内没有符合条件的瓦片")
        }
        if (variableKeys.nonEmpty) {
          tileSql ++= "variable_key in " + variableKeys
        }
        else {
          throw new IllegalArgumentException("选择变量内没有符合条件的瓦片")
        }
        println(tileSql)
        val tileResults: ResultSet = statement.executeQuery(tileSql.toString())
        val tileKeys = new StringBuilder
        if (tileResults.first()) {
          tileResults.previous()
          tileKeys ++= "("
          while (tileResults.next) {
            tileKeys ++= "\'"
            tileKeys ++= tileResults.getString(1)
            tileKeys ++= "\',"
          }
          tileKeys.deleteCharAt(tileKeys.length - 1)
          tileKeys ++= ")"
        }
        println("tile keys are " + tileKeys)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val taskID: String = Random.nextInt().toString
    val level = 0
    val windowRange = "[103.6184, 1.25, 103.98265, 1.444]"
    getMeteorologyTileFact(taskID, level, windowRange, "2020-01-01 00:00:00", "2020-01-01 00:00:00", "ERA5_hourly_data_on_single_levels_from_1940_to_present_Reanalysis", "10m_u_component_of_wind")
  }

}
