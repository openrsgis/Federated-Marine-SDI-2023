package whu.edu.cn.geostreamcube.scala.util

import scala.collection.immutable

object ConstantUtil {
  // Redis 基础配置
  final val JEDIS_HOST: String = "125.220.153.26"
  final val JEDIS_PORT: Int = 6379
  final val JEDIS_PWD: String = "ypfamily608"
  // Redis 超时时间
  final val REDIS_CACHE_TTL: Long = 2 * 60L
  // MinIO 基础配置
  final val MINIO_ENDPOINT: String = "http://125.220.153.22:9006"
  final val MINIO_ACCESS_KEY: String = "rssample"
  final val MINIO_SECRET_KEY: String = "ypfamily608"
  final val MINIO_BUCKET_NAME: String = "geo-stream-cube"
  final val MINIO_HEAD_SIZE: Int = 350000
  final val MINIO_MAX_CONNECTIONS: Int = 100
  // PostgreSQL 基础配置
  final val POSTGRESQL_URL: String = "jdbc:postgresql://125.220.153.25:5432/FMSDI_Testbed19"
  final val POSTGRESQL_DRIVER: String = "org.postgresql.Driver"
  final val POSTGRESQL_USER: String = "postgres"
  final val POSTGRESQL_PWD: String = "ypfamily608"

  final val WORK_PREFIX: String = "oge:computation_ogc:existedTiles:"
  final val HEAD_SIZE: Long = 262143
  final val epsilon: Double = 1e-6

  // Tile大小
  final val meteorologyTileByteCount: Int = 524689

  final val TOMCAT_DIR: String = "/home/geocube/tomcat8/apache-tomcat-8.5.57/webapps/geostreamcube/"


  // TMS 分辨率
  final val TMS_RESOLUTION: immutable.Map[Int, Double] = Map(
    1 -> 0.703125,
    2 -> 0.3515625,
    3 -> 0.17578125,
    4 -> 0.087890625,
    5 -> 0.0439453125,
    6 -> 0.0219726563,
    7 -> 0.0109863281,
    8 -> 0.0054931641,
    9 -> 0.0027465820,
    10 -> 0.0013732910,
    11 -> 0.0006866455,
    12 -> 0.0003433228,
    13 -> 0.0001716614,
    14 -> 0.0000858307,
    15 -> 0.0000429153,
    16 -> 0.0000214577,
    17 -> 0.0000107288,
    18 -> 0.0000053644,
    19 -> 0.0000026822,
    20 -> 0.0000013411,
    21 -> 0.0000006706
  )
}
