package whu.edu.cn.geostreamcube.scala.datapreprocessing

import geotrellis.layer._
import geotrellis.proj4.{CRS, LatLng}
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
import geotrellis.raster.resample.{Bilinear, CubicConvolution}
import geotrellis.raster.{CellType, DoubleConstantNoDataCellType, Tile}
import geotrellis.spark._
import geotrellis.spark.pyramid.Pyramid
import geotrellis.spark.store.hadoop.HadoopSparkContextMethodsWrapper
import geotrellis.store.index.{KeyIndex, ZCurveKeyIndexMethod}
import geotrellis.vector.{Extent, ProjectedExtent}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import java.io.File

object FileWriterUtil {

  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Tiler")

    val sc = new SparkContext(conf)

    val productName: String = "era5"
    val variableName: String = "NDVI"
    try {
      val FOLDER_PATH: String = "D:/组内项目/实时Cube/data/" + productName + "/" + variableName + "/"
      val folder = new File(FOLDER_PATH)
      val files: Array[File] = folder.listFiles

      if (files != null) for (file <- files) {
        if (file.isFile) {
          val filePath: String = file.getAbsolutePath
          val fileName: String = file.getName.replace(".tif", "")
          write2File(sc, productName, variableName, fileName, filePath, LatLng, 0.1, DoubleConstantNoDataCellType)
        }
      }

    } finally {
      sc.stop()
    }
  }

  def write2File(implicit sc: SparkContext, productName: String, variableName: String, fileName: String, filePath: String, crs: CRS, res: Double, cellType: CellType): Unit = {

    val rddFile: RDD[(ProjectedExtent, Tile)] = sc.hadoopGeoTiffRDD(filePath)
    val first: (ProjectedExtent, Tile) = rddFile.first()
    val firstTileExtent: Extent = rddFile.first()._1.extent
    val firstTile: Tile = first._2
    val resOriginX: Double = (firstTileExtent.xmax - firstTileExtent.xmin) / firstTile.cols
    val resRatioX: Double = resOriginX / res
    val resOriginY: Double = (firstTileExtent.ymax - firstTileExtent.ymin) / firstTile.rows
    val resRatioY: Double = resOriginY / res
    val rdd: RDD[(ProjectedExtent, Tile)] = rddFile.map(t => {
      (t._1, t._2.resample((t._2.cols * resRatioX).toInt, (t._2.rows * resRatioY).toInt, CubicConvolution))
    })


    val zoomedLayoutScheme: LayoutScheme = ZoomedLayoutScheme(LatLng, 256)
    val (zoom: Int, metadata: TileLayerMetadata[SpatialKey]) = rdd.collectMetadata[SpatialKey](zoomedLayoutScheme)
    val originRdd: RDD[(SpatialKey, Tile)] with Metadata[TileLayerMetadata[SpatialKey]] = rdd.tileToLayout[SpatialKey](metadata)

    val keyIndex: KeyIndex[SpatialKey] = ZCurveKeyIndexMethod.createIndex(null)

    val extentOrigin: Extent = originRdd.metadata.extent
    val extentNew: Extent = LatLng.worldExtent

    val minExtent: Extent = Extent(math.max(extentOrigin.xmin, extentNew.xmin), math.max(extentOrigin.ymin, extentNew.ymin), math.min(extentOrigin.xmax, extentNew.xmax), math.min(extentOrigin.ymax, extentNew.ymax))

    Pyramid.upLevels(originRdd, zoomedLayoutScheme, zoom, Bilinear) { (rdd, z) =>
      rdd.foreach(layer => {
        val key: SpatialKey = layer._1
        val tile: Tile = layer._2.convert(cellType)
        val zCurveIndex: BigInt = keyIndex.toIndex(SpatialKey(key.col, key.row))
        val extentR: Extent = rdd.metadata.layout.mapTransform(key)

        val resolution: Double = rdd.metadata.cellSize.resolution

        if ((extentR.xmax - minExtent.xmin) >= resolution && (extentR.xmin - minExtent.xmax) <= -resolution && (extentR.ymax - minExtent.ymin) >= resolution && (extentR.ymin - minExtent.ymax) <= -resolution && !tile.isNoDataTile) {
          val geoTiff: SinglebandGeoTiff = SinglebandGeoTiff(tile, extentR, crs)
          val folderPath: String = "D:/组内项目/实时Cube/processed_data/" + productName + "/" + variableName + "/" + fileName + "/" + z
          val folder = new File(folderPath)
          if (!folder.exists()) {
            val created: Boolean = folder.mkdirs()
            if (created) {
              println("Folder created successfully.")
            } else {
              println("Failed to create folder.")
            }
          } else {
            println("Folder already exists.")
          }
          GeoTiffWriter.write(geoTiff, folderPath + "/" + zCurveIndex.toString() + ".tif", optimizedOrder = true)
        }
      })
    }
  }

}
