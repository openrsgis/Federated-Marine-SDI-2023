package whu.edu.cn.geostreamcube.scala.deprecated

import io.minio.{GetObjectArgs, MinioClient}
import whu.edu.cn.geostreamcube.scala.util.ConstantUtil.{MINIO_ACCESS_KEY, MINIO_ENDPOINT, MINIO_SECRET_KEY}

import java.io.{ByteArrayOutputStream, InputStream}

object MinIOReaderUtil {
  def main(args: Array[String]): Unit = {
    val minioClient: MinioClient = MinioClient.builder()
      .endpoint(MINIO_ENDPOINT)
      .credentials(MINIO_ACCESS_KEY, MINIO_SECRET_KEY)
      .build()
    minioClient.setTimeout(1000, 1000, 1000)

    val inputStream: InputStream = minioClient.getObject(GetObjectArgs.builder.bucket("geo-stream-cube").`object`("1884847.tiff").build)
    val outStream = new ByteArrayOutputStream
    val buffer = new Array[Byte](256 * 256)
    var len = 0
    var a = 0
    while ( {
      len = inputStream.read(buffer)
      len != -1
    }) {
      a = len
      outStream.write(buffer, 0, len)
    }
    inputStream.close()
    outStream.close()
  }


}
