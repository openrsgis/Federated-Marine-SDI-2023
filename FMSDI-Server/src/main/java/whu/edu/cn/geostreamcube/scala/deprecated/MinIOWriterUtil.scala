//package whu.edu.cn.geostreamcube.util
//
//import io.minio.MinioClient
//import geotrellis.layer._
//import geotrellis.proj4._
//import geotrellis.raster._
//import geotrellis.raster.io.geotiff.SinglebandGeoTiff
//import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
//import geotrellis.spark._
//import geotrellis.spark.store.hadoop._
//import geotrellis.store.index.{KeyIndex, ZCurveKeyIndexMethod}
//import geotrellis.vector._
//import org.apache.spark._
//import org.apache.spark.rdd._
//import whu.edu.cn.geostreamcube.util.MinIOUtil
//
//
//object MinIOWriterUtil {
//
//
//  def main(args: Array[String]): Unit = {
//    val inputPath = "D:\\组内项目\\实时Cube\\data\\2m_temperature\\2m_temperature_20200101T000000Z.tif"
//    val outputPath = "D:/cog/test/"
//
//
//    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Tiler")
//
//    val sc = new SparkContext(conf)
//    try {
//      run(sc, inputPath, Extent(-180, -90, 180, 90), 0.1, LatLng, 256, ShortConstantNoDataCellType)
//    } finally {
//      sc.stop()
//    }
//  }
//
//  // 切分Cube需要以下5个参数：范围、分辨率、坐标系、瓦片大小、数据类型
//  // 这里的extent是4326的，需要转换为crs下的范围
//  // 这里的resolution由用户指定，必须是crs下的，否则会出错，我们没有义务将4326下的分辨率转为crs下的，也没有这种转换方法
//  def run(implicit sc: SparkContext, inputPath: String, extentLatLng: Extent, resolution: Double, crs: CRS, tileSize: Int, cellType: CellType): Unit = {
//
//    val rdd: RDD[(ProjectedExtent, Tile)] = sc.hadoopGeoTiffRDD(inputPath)
//    val extentOriginMinMax: (Double, Double, Double, Double) = rdd.map(t => {
//      val extentInternal: Extent = t._1.extent
//      (extentInternal.xmin, extentInternal.ymin, extentInternal.xmax, extentInternal.ymax)
//    }).reduce((a, b) => {
//      (math.min(a._1, b._1), math.min(a._2, b._2), math.max(a._3, b._3), math.max(a._4, b._4))
//    })
//    val extentOrigin: Extent = Extent(extentOriginMinMax._1, extentOriginMinMax._2, extentOriginMinMax._3, extentOriginMinMax._4).reproject(rdd.first()._1.crs, crs)
//    val localLayoutScheme: LayoutScheme = FloatingLayoutScheme(512)
//    val (_: Int, metadata: TileLayerMetadata[SpatialKey]) = rdd.collectMetadata[SpatialKey](localLayoutScheme)
//    val originRdd: RDD[(SpatialKey, Tile)] with Metadata[TileLayerMetadata[SpatialKey]] = rdd.tileToLayout[SpatialKey](metadata)
//
//    val extent: Extent = extentLatLng.reproject(LatLng, crs)
//
//    // 直接进行Cube切分
//    val layoutCols: Int = math.max(math.ceil((extent.xmax - extent.xmin) / resolution / tileSize).toInt, 1)
//    val layoutRows: Int = math.max(math.ceil((extent.ymax - extent.ymin) / resolution / tileSize).toInt, 1)
//    val tileLayout: TileLayout = TileLayout(layoutCols, layoutRows, tileSize, tileSize)
//    val extentNew: Extent = new Extent(extent.xmin, extent.ymin, extent.xmin + resolution * tileSize * layoutCols, extent.ymin + resolution * tileSize * layoutRows)
//    val layoutDefinition: LayoutDefinition = LayoutDefinition(extentNew, tileLayout)
//
//    val tiledRdd: RDD[(SpatialKey, Tile)] with Metadata[TileLayerMetadata[SpatialKey]] = originRdd.reproject(crs, layoutDefinition)._2
//
//    val minExtent: Extent = Extent(math.max(extentOrigin.xmin, extentNew.xmin), math.max(extentOrigin.ymin, extentNew.ymin), math.min(extentOrigin.xmax, extentNew.xmax), math.min(extentOrigin.ymax, extentNew.ymax))
//    val keyIndex: KeyIndex[SpatialKey] = ZCurveKeyIndexMethod.createIndex(null)
//
//    tiledRdd.foreach(layer => {
//      val key: SpatialKey = layer._1
//      val tile: Tile = layer._2.convert(cellType)
//      val zCurveIndex: BigInt = keyIndex.toIndex(SpatialKey(key.col, key.row))
//      val extentR: Extent = tiledRdd.metadata.layout.mapTransform(key)
//      if ((extentR.xmax - minExtent.xmin) >= resolution && (extentR.xmin - minExtent.xmax) <= -resolution && (extentR.ymax - minExtent.ymin) >= resolution && (extentR.ymin - minExtent.ymax) <= -resolution && !tile.isNoDataTile) {
//        val minioClient: MinioClient = new MinIOUtil().getMinioClient
//
//        val geoTiff: SinglebandGeoTiff = SinglebandGeoTiff(tile, extentR, crs)
//
//        GeoTiffWriter.write(geoTiff, outputPath + zCurveIndex.toString() + ".tif", optimizedOrder = true)
//      }
//    })
//
//  }
//}
