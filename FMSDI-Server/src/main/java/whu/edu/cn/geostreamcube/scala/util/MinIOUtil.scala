package whu.edu.cn.geostreamcube.scala.util

import io.minio._
import ConstantUtil.{MINIO_ACCESS_KEY, MINIO_BUCKET_NAME, MINIO_ENDPOINT, MINIO_MAX_CONNECTIONS, MINIO_SECRET_KEY, meteorologyTileByteCount}

import java.io.{ByteArrayOutputStream, InputStream}

class MinIOUtil {

  private val connectionPool: Array[MinioClient] = Array.fill(MINIO_MAX_CONNECTIONS)(createMinioClient)

  private def createMinioClient: MinioClient = {
    val minioClient: MinioClient = MinioClient.builder()
      .endpoint(MINIO_ENDPOINT)
      .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
      .build()
    minioClient
  }

  def getMinioClient: MinioClient = {
    // 从连接池中获取可用的 MinioClient
    val client: Option[MinioClient] = connectionPool.synchronized {
      connectionPool.find(_ != null)
    }

    if (client.isDefined) {
      client.get
    } else {
      throw new Exception("No available MinioClient in the connection pool.")
    }
  }

  def releaseMinioClient(client: MinioClient): Unit = {
    // 将 MinioClient 放回连接池
    connectionPool.synchronized {
      val index: Int = connectionPool.indexOf(client)
      if (index != -1) {
        connectionPool(index) = null
      }
    }
  }
}

object MinIOUtil {
  def getTile(path: String): Array[Byte] = {
    val minioClient: MinioClient = MinioClient.builder()
      .endpoint(MINIO_ENDPOINT)
      .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
      .build()

    val inputStream: InputStream = minioClient.getObject(GetObjectArgs.builder.bucket(MINIO_BUCKET_NAME).`object`(path).build)
    val outStream = new ByteArrayOutputStream
    val buffer = new Array[Byte](meteorologyTileByteCount)
    var len = 0
    while ( {
      len = inputStream.read(buffer)
      len != -1
    }) outStream.write(buffer, 0, len)
    val outByteArray: Array[Byte] = outStream.toByteArray
    inputStream.close()
    outStream.close()
    outByteArray
  }

  def getTile(minioClient: MinioClient, path: String): Array[Byte] = {
    val inputStream: InputStream = minioClient.getObject(GetObjectArgs.builder.bucket(MINIO_BUCKET_NAME).`object`(path).build)
    val outStream = new ByteArrayOutputStream
    val buffer = new Array[Byte](meteorologyTileByteCount)
    var len = 0
    while ( {
      len = inputStream.read(buffer)
      len != -1
    }) outStream.write(buffer, 0, len)
    val outByteArray: Array[Byte] = outStream.toByteArray
    inputStream.close()
    outStream.close()
    outByteArray
  }
}
