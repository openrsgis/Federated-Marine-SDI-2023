package whu.edu.cn.geostreamcube.scala.application

import com.alibaba.fastjson._
import geotrellis.layer.SpatialKey
import geotrellis.raster.Tile
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.io.geotiff.reader.GeoTiffReader.readSingleband
import geotrellis.vector.Extent
import io.minio.MinioClient
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import whu.edu.cn.geostreamcube.entity.MeteorologyTile
import whu.edu.cn.geostreamcube.scala.entity.MeteorologyTilePro
import whu.edu.cn.geostreamcube.scala.util.ConstantUtil.{MINIO_ACCESS_KEY, MINIO_ENDPOINT, MINIO_SECRET_KEY, TOMCAT_DIR}
import whu.edu.cn.geostreamcube.scala.util.MeteorologyRenderUtil._
import whu.edu.cn.geostreamcube.scala.util.MinIOUtil.getTile
import whu.edu.cn.geostreamcube.scala.util.WebTileUtil.zCurveToXY

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util
import scala.collection.JavaConverters._


object Visualization {

  def MeteorologyVisBak(implicit sc: SparkContext, meteorologyTile: util.List[MeteorologyTile], UUID: String, partitions: Int): JSONObject = {
    val time1: Long = System.currentTimeMillis()
    val originRdd: RDD[MeteorologyTile] = sc.makeRDD(meteorologyTile.asScala, partitions)
    originRdd.foreachPartition(x => {
      val minioClient: MinioClient = MinioClient.builder()
        .endpoint(MINIO_ENDPOINT)
        .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
        .build()
      x.foreach(t => {
        val time1: Long = System.currentTimeMillis()
        val tileByteArray: Array[Byte] = getTile(minioClient, t.getTilePath)
        val time2: Long = System.currentTimeMillis()
        println("Get Tile Time is " + (time2 - time1))
        val pathArray: Array[String] = t.getTilePath.replace(".tif", "").split("/")
        val productName: String = pathArray(0)
        val variableName: String = pathArray(1)
        val timeStamp: String = pathArray(2).replace("T", "").replace("Z", "")
        val zCurveIndex: Int = pathArray(4).toInt
        val geotiff: SinglebandGeoTiff = readSingleband(tileByteArray)
        val tile: Tile = geotiff.tile

        val folderPath: String = TOMCAT_DIR + UUID + "/"
        val folder = new File(folderPath)
        if (!folder.exists()) {
          val created: Boolean = folder.mkdirs()
          if (created) {
            println("Folder created successfully.")
          } else {
            println("Failed to create folder.")
          }
        }
        val pathWriter: String = folderPath + productName + "_" + variableName + "_" + zCurveIndex + "_" + timeStamp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".png"
        variableName match {
          case "2m_temperature" =>
            tile.renderPng(TWO_M_TEMPERATURE_RENDER).write(pathWriter)
          case "total_precipitation" =>
            tile.renderPng(TOTAL_PRECIPITATION).write(pathWriter)
          case "sea_surface_temperature" =>
            tile.renderPng(SEA_SURFACE_TEMPERATURE).write(pathWriter)
          case "significant_height_of_combined_wind_waves_and_swell" =>
            tile.renderPng(SIGNIFICANT_HEIGHT_WAVE).write(pathWriter)
          case "mean_sea_level_pressure" =>
            tile.renderPng(MEAN_SEA_LEVEL_PRESSURE).write(pathWriter)
          case "soil_type" =>
            tile.renderPng(SOIL_TYPE).write(pathWriter)
          case "total_cloud_cover" =>
            tile.renderPng(TOTAL_CLOUD_COVER).write(pathWriter)
          case "air_density_over_the_oceans" =>
            tile.renderPng(AIR_DENSITY_OVER_THE_OCEANS).write(pathWriter)
          case "high_vegetation_cover" =>
            tile.renderPng(HIGH_VEGETATION_COVER).write(pathWriter)
          case "NDVI" =>
            tile.renderPng(HIGH_VEGETATION_COVER).write(pathWriter)
          case "10m_u_component_of_wind" =>
            tile.renderPng().write(pathWriter)
          case "10m_v_component_of_wind" =>
            tile.renderPng().write(pathWriter)
          case "mean_temperature" =>
            tile.renderPng(MEAN_TEMPERATURE_RENDER).write(pathWriter)
        }
      })
    })

    val time2: Long = System.currentTimeMillis()
    println("Total time is " + (time2 - time1))

    new JSONObject()
  }


  def MeteorologyVis(implicit sc: SparkContext, meteorologyTile: util.List[MeteorologyTile], UUID: String, partitions: Int): JSONObject = {
    val time1: Long = System.currentTimeMillis()
    val originRdd: RDD[MeteorologyTile] = sc.makeRDD(meteorologyTile.asScala, partitions)
    val meteorologyRdd: RDD[MeteorologyTilePro] = originRdd.map(t => {
      val time1: Long = System.currentTimeMillis()
      val tileByteArray: Array[Byte] = getTile(t.getTilePath)
      val time2: Long = System.currentTimeMillis()
      println("Get Tile Time is " + (time2 - time1))
      val pathArray: Array[String] = t.getTilePath.replace(".tif", "").split("/")
      val productName: String = pathArray(0)
      val variableName: String = pathArray(1)
      val timeStamp: String = pathArray(2).replace("T", "").replace("Z", "")
      val zoom: Int = pathArray(3).toInt
      val zCurveIndex: Int = pathArray(4).toInt
      val xyIndex: Array[Int] = zCurveToXY(zCurveIndex, zoom)
      val geotiff: SinglebandGeoTiff = readSingleband(tileByteArray)
      val tile: Tile = geotiff.tile
      val extent: Extent = geotiff.extent

      val meteorologyTilePro: MeteorologyTilePro = new MeteorologyTilePro
      meteorologyTilePro.setProductName(productName)
      meteorologyTilePro.setVariableName(variableName)
      meteorologyTilePro.setExtent(extent)
      meteorologyTilePro.setTime(timeStamp)
      meteorologyTilePro.setZoom(zoom)
      meteorologyTilePro.setZCurveIndex(zCurveIndex)
      meteorologyTilePro.setSpatialKey(SpatialKey(xyIndex(0), xyIndex(1)))
      meteorologyTilePro.setTile(tile)
      meteorologyTilePro
    }).persist()

    val productName: Array[String] = meteorologyRdd.map(t => t.getProductName).distinct().sortBy(t => t).collect()
    val variableName: Array[String] = meteorologyRdd.map(t => t.getVariableName).distinct().sortBy(t => t).collect()
    val extentKey: Array[String] = meteorologyRdd.map(t => t.getZCurveIndex).distinct().sortBy(t => t).map(t => t.toString).collect()
    val extentXMin: Array[String] = meteorologyRdd.map(t => (t.getZCurveIndex, t.getExtent.xmin)).distinct().sortBy(t => t._1).map(t => t._2.toString).collect()
    val extentYMin: Array[String] = meteorologyRdd.map(t => (t.getZCurveIndex, t.getExtent.ymin)).distinct().sortBy(t => t._1).map(t => t._2.toString).collect()
    val extentXMax: Array[String] = meteorologyRdd.map(t => (t.getZCurveIndex, t.getExtent.xmax)).distinct().sortBy(t => t._1).map(t => t._2.toString).collect()
    val extentYMax: Array[String] = meteorologyRdd.map(t => (t.getZCurveIndex, t.getExtent.ymax)).distinct().sortBy(t => t._1).map(t => t._2.toString).collect()
    val timeKey: Array[String] = meteorologyRdd.map(t => t.getTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))).distinct().sortBy(t => t).collect()

    val jsonObject: JSONObject = new JSONObject()
    jsonObject.put("productName", JSON.parseArray(productName.mkString("[\"", "\", \"", "\"]")))
    jsonObject.put("variableName", JSON.parseArray(variableName.mkString("[\"", "\", \"", "\"]")))
    jsonObject.put("extentKey", JSON.parseArray(extentKey.mkString("[\"", "\", \"", "\"]")))
    jsonObject.put("timeKey", JSON.parseArray(timeKey.mkString("[\"", "\", \"", "\"]")))
    jsonObject.put("extentXMin", JSON.parseArray(extentXMin.mkString("[\"", "\", \"", "\"]")))
    jsonObject.put("extentYMin", JSON.parseArray(extentYMin.mkString("[\"", "\", \"", "\"]")))
    jsonObject.put("extentXMax", JSON.parseArray(extentXMax.mkString("[\"", "\", \"", "\"]")))
    jsonObject.put("extentYMax", JSON.parseArray(extentYMax.mkString("[\"", "\", \"", "\"]")))


    meteorologyRdd.groupBy(t => t.getProductName).foreach(t => {
      val productName: String = t._1
      t._2.groupBy(y => y.getVariableName).foreach(y => {
        val variableName: String = y._1
        y._2.groupBy(u => u.getTime).foreach(u => {
          val time: LocalDateTime = u._1
          u._2.foreach(i => {
            val folderPath: String = TOMCAT_DIR + UUID + "/"
            val folder = new File(folderPath)
            if (!folder.exists()) {
              val created: Boolean = folder.mkdirs()
              if (created) {
                println("Folder created successfully.")
              } else {
                println("Failed to create folder.")
              }
            }
            val pathWriter: String = folderPath + productName + "_" + variableName + "_" + i.getZCurveIndex + "_" + time.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".png"
            variableName match {
              case "2m_temperature" =>
                i.getTile.renderPng(TWO_M_TEMPERATURE_RENDER).write(pathWriter)
              case "total_precipitation" =>
                i.getTile.renderPng(TOTAL_PRECIPITATION).write(pathWriter)
              case "sea_surface_temperature" =>
                i.getTile.renderPng(SEA_SURFACE_TEMPERATURE).write(pathWriter)
              case "significant_height_of_combined_wind_waves_and_swell" =>
                i.getTile.renderPng(SIGNIFICANT_HEIGHT_WAVE).write(pathWriter)
              case "mean_sea_level_pressure" =>
                i.getTile.renderPng(MEAN_SEA_LEVEL_PRESSURE).write(pathWriter)
              case "soil_type" =>
                i.getTile.renderPng(SOIL_TYPE).write(pathWriter)
              case "total_cloud_cover" =>
                i.getTile.renderPng(TOTAL_CLOUD_COVER).write(pathWriter)
              case "air_density_over_the_oceans" =>
                i.getTile.renderPng(AIR_DENSITY_OVER_THE_OCEANS).write(pathWriter)
              case "high_vegetation_cover" =>
                i.getTile.renderPng(HIGH_VEGETATION_COVER).write(pathWriter)
              case "NDVI" =>
                i.getTile.renderPng(HIGH_VEGETATION_COVER).write(pathWriter)


              case "10m_u_component_of_wind" =>
                i.getTile.renderPng().write(pathWriter)
              case "10m_v_component_of_wind" =>
                i.getTile.renderPng().write(pathWriter)
              case "mean_temperature" =>
                i.getTile.renderPng(MEAN_TEMPERATURE_RENDER).write(pathWriter)
            }
          })
        })
      })
    })

    meteorologyRdd.unpersist()
    val time2: Long = System.currentTimeMillis()
    println("Total time is " + (time2 - time1))

    jsonObject
  }

}