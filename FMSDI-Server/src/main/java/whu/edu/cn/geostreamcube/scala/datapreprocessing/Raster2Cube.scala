package whu.edu.cn.geostreamcube.scala.datapreprocessing

import geotrellis.proj4._
import geotrellis.raster.DoubleConstantNoDataCellType
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark._
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import whu.edu.cn.geostreamcube.scala.util.CustomConsumerUtil
import whu.edu.cn.geostreamcube.scala.deprecated.WriterUtil.write2File

object Raster2Cube {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("word-count-with-state")
    val ssc = new StreamingContext(conf, Seconds(5))
    ssc.checkpoint("./ckp")

    val kafkaParam: Map[String, Object] = new CustomConsumerUtil().kafkaParam
    val topics: Array[String] = Array("geoStreamCube")

    val streams: InputDStream[ConsumerRecord[String, Array[Byte]]] = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent, ConsumerStrategies.Subscribe(topics, kafkaParam))

    streams.foreachRDD(t => {
      if (!t.isEmpty()) {
        write2File(t.map(_.value()), LatLng, DoubleConstantNoDataCellType)
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
