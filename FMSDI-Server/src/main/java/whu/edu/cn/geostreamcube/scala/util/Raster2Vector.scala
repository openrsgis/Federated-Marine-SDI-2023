package whu.edu.cn.geostreamcube.scala.util

import org.gdal.gdal.{Band, Dataset, gdal}
import org.gdal.gdalconst.gdalconstConstants
import org.gdal.ogr.{DataSource, FieldDefn, Layer, ogr}
import org.gdal.osr.SpatialReference
import org.gdal.ogr.ogrConstants

object Raster2Vector {

  def main(args: Array[String]): Unit = {
    // 初始化GDAL库
    gdal.AllRegister()
    ogr.RegisterAll()

    val inputRaster = "D:\\组内项目\\实时Cube\\processed_data\\era5\\significant_height_of_combined_wind_waves_and_swell\\significant_height_of_combined_wind_waves_and_swell_20200101T000000Z\\0\\0.tif"
    val outputShapefile = "D:\\apache-tomcat-9.0.38\\webapps\\geostreamcube\\cadaster_raster\\aaa.shp"

    rasterToShp(inputRaster, outputShapefile)

  }

  /**
   * 栅格矢量化
   *
   * @param inRaster ：输入的栅格数据的文件路径
   * @param outShp   ：输出的矢量数据保存位置
   */
  def rasterToShp(inRaster: String, outShp: String): Unit = { //载入栅格，读取相关信息
    val dataset: Dataset = gdal.Open(inRaster, gdalconstConstants.GA_ReadOnly)
    val band: Band = dataset.GetRasterBand(1) //栅格转矢量需要的波段信息
    val prj = new SpatialReference
    if (dataset.GetProjectionRef.nonEmpty) prj.ImportFromWkt(dataset.GetProjectionRef) //栅格数据的坐标系作为矢量化后的坐标系
    //创建输出矢量
    val fileName: String = outShp.substring(outShp.lastIndexOf("\\") + 1) //带后缀的文件名
    val name: String = fileName.substring(0, fileName.lastIndexOf(".")) //不带后缀

    val dataSource: DataSource = ogr.GetDriverByName("ESRI Shapefile").CreateDataSource(outShp)


    val layer: Layer = dataSource.CreateLayer(name, prj)
    val field = new FieldDefn("height", ogrConstants.OFTReal) //创建一个字段用来存储栅格的像素值
    layer.CreateField(field, 1)
    //矢量化
    gdal.Polygonize(band, null, layer, 0)
    layer.SyncToDisk
  }
}


