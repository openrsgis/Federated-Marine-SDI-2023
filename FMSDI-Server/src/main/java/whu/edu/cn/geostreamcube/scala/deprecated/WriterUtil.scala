package whu.edu.cn.geostreamcube.scala.deprecated

import geotrellis.layer._
import geotrellis.proj4.{CRS, LatLng}
import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.io.geotiff.compression.NoCompression
import geotrellis.raster.io.geotiff.reader.GeoTiffReader.readSingleband
import geotrellis.raster.io.geotiff.writer.GeoTiffWriter
import geotrellis.raster.resample.Bilinear
import geotrellis.raster.{CellType, Tile}
import geotrellis.spark._
import geotrellis.spark.pyramid.Pyramid
import geotrellis.spark.store.cog.COGLayer
import geotrellis.spark.store.cog.COGLayerWriter.Options.{compressionToOption, maxTileSizeToOptions}
import geotrellis.spark.store.s3.cog.S3COGLayerWriter
import geotrellis.store.cog.ZoomRange
import geotrellis.store.index.{KeyIndex, ZCurveKeyIndexMethod}
import geotrellis.store.s3.S3AttributeStore
import geotrellis.vector.{Extent, ProjectedExtent}
import org.apache.commons.lang.SerializationUtils.deserialize
import org.apache.spark.rdd.RDD
import software.amazon.awssdk.services.s3.S3Client
import whu.edu.cn.geostreamcube.scala.util.ConstantUtil.MINIO_BUCKET_NAME
import whu.edu.cn.geostreamcube.scala.util.S3Util

import java.io.File

object WriterUtil {

  def write2File(tiffRdd: RDD[Array[Byte]], crs: CRS, cellType: CellType): Unit = {
    val fileName: String = deserialize(tiffRdd.first()).asInstanceOf[(String, Array[Byte])]._1
    val rdd: RDD[(ProjectedExtent, Tile)] = tiffRdd.map(t => {
      val tuple: (String, String) = deserialize(t).asInstanceOf[(String, String)]
      val geotiff: SinglebandGeoTiff = readSingleband(tuple._2)
      val tile: Tile = geotiff.tile
      val extent: ProjectedExtent = ProjectedExtent(geotiff.extent, geotiff.crs)
      (extent, tile)
    })

    val zoomedLayoutScheme: LayoutScheme = ZoomedLayoutScheme(LatLng, 256)
    val (zoom: Int, metadata: TileLayerMetadata[SpatialKey]) = rdd.collectMetadata[SpatialKey](zoomedLayoutScheme)
    val originRdd: RDD[(SpatialKey, Tile)] with Metadata[TileLayerMetadata[SpatialKey]] = rdd.tileToLayout[SpatialKey](metadata)
    val keyIndex: KeyIndex[SpatialKey] = ZCurveKeyIndexMethod.createIndex(null)

    Pyramid.upLevels(originRdd, zoomedLayoutScheme, zoom, Bilinear) { (rdd, z) =>
      rdd.foreach(layer => {
        val key: SpatialKey = layer._1
        val tile: Tile = layer._2.convert(cellType)
        val zCurveIndex: BigInt = keyIndex.toIndex(SpatialKey(key.col, key.row))
        val extentR: Extent = rdd.metadata.layout.mapTransform(key)
        val geoTiff: SinglebandGeoTiff = SinglebandGeoTiff(tile, extentR, crs)
        val folderPath: String = "D:/组内项目/实时Cube/era5/" + fileName + "/" + z

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
      })
    }
  }


  def write2S3(tiffRdd: RDD[Array[Byte]], crs: CRS, cellType: CellType): Unit = {
    val tileSize = 256

    val fileName: String = deserialize(tiffRdd.first()).asInstanceOf[(String, Array[Byte])]._1
    val inputRdd: RDD[(ProjectedExtent, Tile)] = tiffRdd.map(t => {
      val tuple: (String, String) = deserialize(t).asInstanceOf[(String, String)]
      val geotiff: SinglebandGeoTiff = readSingleband(tuple._2)
      val tile: Tile = geotiff.tile
      val extent: ProjectedExtent = ProjectedExtent(geotiff.extent, geotiff.crs)
      (extent, tile)
    })

    val localLayoutScheme: FloatingLayoutScheme = FloatingLayoutScheme(tileSize)
    val (_: Int, rasterMetaData: TileLayerMetadata[SpatialKey]) =
      inputRdd.collectMetadata[SpatialKey](localLayoutScheme)

    val tiled: RDD[(SpatialKey, Tile)] = inputRdd.tileToLayout(cellType, rasterMetaData.layout)

    val layoutScheme: ZoomedLayoutScheme = ZoomedLayoutScheme(crs, tileSize = 256)

    val (zoom, reprojected): (Int, RDD[(SpatialKey, Tile)] with Metadata[TileLayerMetadata[SpatialKey]]) =
      TileLayerRDD(tiled, rasterMetaData).reproject(crs, layoutScheme)

    val prefix: String = "test"


    val cogLayer: COGLayer[SpatialKey, Tile] =
      COGLayer.fromLayerRDD(
        reprojected,
        zoom,
        options = {
          compressionToOption(NoCompression)
          maxTileSizeToOptions(tileSize)
        }
      )
    val keyIndexes: Map[ZoomRange, KeyIndex[SpatialKey]] =
      cogLayer.metadata.zoomRangeInfos.
        map { case (zr, bounds) => zr -> ZCurveKeyIndexMethod.createIndex(bounds) }.
        toMap

    // 创建 S3 客户端
    val s3Client: S3Client = new S3Util().getS3Client
    val attributeStore: S3AttributeStore = S3AttributeStore(MINIO_BUCKET_NAME, prefix, s3Client)
    val writer: S3COGLayerWriter = S3COGLayerWriter(attributeStore)
    writer.writeCOGLayer(fileName, cogLayer, keyIndexes)
  }


}
