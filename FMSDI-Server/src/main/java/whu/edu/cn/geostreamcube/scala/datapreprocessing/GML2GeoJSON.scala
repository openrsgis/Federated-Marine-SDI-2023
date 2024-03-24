package whu.edu.cn.geostreamcube.scala.datapreprocessing

import org.gdal.ogr.{DataSource, Layer, ogr}

import java.io.File


object GML2GeoJSON {

  def main(args: Array[String]): Unit = {
    // 输入的GML文件路径
    val inputGmlFile: String = "D:/组内项目/实时Cube/data/3D/SLA_BLDG_FFC3742A-E39F-4178-B149-15C0DFA6BB98.gml"

    // 转换后的GeoJSON文件路径
    val outputGeoJsonFile: String = "D:/组内项目/实时Cube/3D/output.geojson"

    // 转换为GeoJSON
    convertGmlToGeoJson(inputGmlFile, outputGeoJsonFile)
  }

  def convertGmlToGeoJson(gmlFile: String, geoJsonFile: String): Unit = {
    val command = Seq("ogr2ogr", "-f", "GeoJSON", geoJsonFile, gmlFile)
    val processBuilder = new ProcessBuilder(command: _*)
    processBuilder.directory(new File("D:/Program Files/QGIS 3.20.0/bin")) // 设置命令执行的目录，默认为当前目录

    val process = processBuilder.start()
    process.waitFor()
  }


}
