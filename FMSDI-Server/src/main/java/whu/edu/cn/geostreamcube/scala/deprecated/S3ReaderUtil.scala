package whu.edu.cn.geostreamcube.scala.deprecated

import geotrellis.layer.{Metadata, SpatialKey, TileLayerMetadata}
import geotrellis.raster.Tile
import geotrellis.spark.store.s3.S3LayerReader
import geotrellis.store.LayerId
import geotrellis.store.s3.S3AttributeStore
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.{S3Client, S3Configuration}
import whu.edu.cn.geostreamcube.scala.util.ConstantUtil._

import java.net.URI

object S3ReaderUtil {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Spark Tiler")
    val sc: SparkContext = new SparkContext(conf)

    // 配置 MINIO 的访问凭证
    val credentials: AwsBasicCredentials = AwsBasicCredentials.create(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
    val credentialsProvider: StaticCredentialsProvider = StaticCredentialsProvider.create(credentials)
    // 配置 MINIO 的服务端点和区域
    val region: Region = Region.of("zh-east-1")
    // 创建 S3 客户端
    val s3Client: S3Client = S3Client.builder()
      .region(region)
      .endpointOverride(URI.create(MINIO_ENDPOINT))
      .credentialsProvider(credentialsProvider)
      .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
      .build()

    val prefix: String = "geo-stream-cube"
    val store: S3AttributeStore = S3AttributeStore(MINIO_BUCKET_NAME, prefix, s3Client)
    val reader: S3LayerReader = S3LayerReader(store, s3Client)(sc)
    /* Needs the implicit SparkContext */
    val layerId: LayerId = LayerId("test", 0) // 图层标识符（由目录和图层名称组成)
    val rdd: RDD[(SpatialKey, Tile)] with Metadata[TileLayerMetadata[SpatialKey]] = reader.read[SpatialKey, Tile, TileLayerMetadata[SpatialKey]](layerId)
    val a: Array[(SpatialKey, Tile)] = rdd.collect()

    println("*************************************")
  }
}
