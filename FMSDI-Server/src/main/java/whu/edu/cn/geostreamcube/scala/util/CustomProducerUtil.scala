package whu.edu.cn.geostreamcube.scala.util

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization._

import java.util.Properties

class CustomProducerUtil {
  // 0 配置
  val properties = new Properties
  // 连接集群 bootstrap.servers
  properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "gisweb1:9092, gisweb3:9092")

  // 指定对应的key和value的序列化类型
  properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[ByteArraySerializer].getName)

  //  properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 16777216)
  //  properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 1048576000)
  properties.put(ProducerConfig.LINGER_MS_CONFIG, 100)

}
