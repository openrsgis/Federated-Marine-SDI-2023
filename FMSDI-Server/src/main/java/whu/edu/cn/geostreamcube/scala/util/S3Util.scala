package whu.edu.cn.geostreamcube.scala.util

import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.{S3Client, S3Configuration}
import ConstantUtil.{MINIO_ACCESS_KEY, MINIO_ENDPOINT, MINIO_MAX_CONNECTIONS, MINIO_SECRET_KEY}

import java.net.URI

class S3Util extends Serializable {

  @transient private val connectionPool: Array[S3Client] = Array.fill(MINIO_MAX_CONNECTIONS)(createS3Client)

  private def createS3Client: S3Client = {
    // 配置 MINIO 的访问凭证
    val credentials: AwsBasicCredentials = AwsBasicCredentials.create(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
    val credentialsProvider: StaticCredentialsProvider = StaticCredentialsProvider.create(credentials)
    // 配置 MINIO 的服务端点和区域
    val region: Region = Region.of("zh-east-1")
    @transient lazy val s3Client: S3Client = S3Client.builder()
      .region(region)
      .endpointOverride(URI.create(MINIO_ENDPOINT))
      .credentialsProvider(credentialsProvider)
      .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
      .build()
    s3Client
  }

  def getS3Client: S3Client = {
    // 从连接池中获取可用的 MinioClient
    val client: Option[S3Client] = connectionPool.synchronized {
      connectionPool.find(_ != null)
    }

    if (client.isDefined) {
      client.get
    } else {
      throw new Exception("No available MinioClient in the connection pool.")
    }
  }

  def releaseS3Client(client: S3Client): Unit = {
    // 将 MinioClient 放回连接池
    connectionPool.synchronized {
      val index: Int = connectionPool.indexOf(client)
      if (index != -1) {
        connectionPool(index) = null
      }
    }
  }
}
