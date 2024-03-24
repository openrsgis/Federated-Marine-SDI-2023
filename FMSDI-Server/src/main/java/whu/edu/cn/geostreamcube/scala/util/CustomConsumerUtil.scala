package whu.edu.cn.geostreamcube.scala.util

import org.apache.kafka.common.serialization._

class CustomConsumerUtil {

  val kafkaParam: Map[String, Object] = Map[String, Object](
    "bootstrap.servers" -> "gisweb1:9092, gisweb3:9092",
    // 指定key的反序列化方式
    "key.deserializer" -> classOf[StringDeserializer],
    // 指定value的反序列化方式
    "value.deserializer" -> classOf[ByteArrayDeserializer],
    "group.id" -> "geoStreamCube",
    /*
    指定消费位置
    earliest 当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
    latest 当个分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
    none topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
     */
    "auto.offset.reset" -> "latest",
    // 如果value合法，自动提交offset
    "enable.auto.commit" -> (true: java.lang.Boolean),

  )

}
