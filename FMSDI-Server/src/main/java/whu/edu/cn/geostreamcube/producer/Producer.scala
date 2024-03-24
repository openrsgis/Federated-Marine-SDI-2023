package whu.edu.cn.geostreamcube.producer

import org.apache.commons.lang.SerializationUtils.serialize
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import whu.edu.cn.geostreamcube.scala.util.CustomProducerUtil


object Producer {
  def main(args: Array[String]): Unit = {
    println("This is the end of the code.")
  }

  def meteorologyProducer(filePath: String, fileName: String): Unit = {
    // 0 获取配置
    val customProps = new CustomProducerUtil
    // 1 创建Kafka的生产者对象
    val kafkaProducer = new KafkaProducer[String, Array[Byte]](customProps.properties)

    val tuple = new Tuple2[String, String](fileName, filePath)

    // 序列化元组
    val serializedTuple: Array[Byte] = serialize(tuple)

    try { // 2 发送数据
      kafkaProducer.send(new ProducerRecord[String, Array[Byte]]("geoStreamCube", serializedTuple))
    } catch {
      case e: Exception =>
        e.printStackTrace()
    } finally {
      // 3 关闭资源
      kafkaProducer.close()
    }
  }
}
