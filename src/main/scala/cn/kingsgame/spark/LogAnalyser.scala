package cn.kingsgame.spark

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * LogAnalyser
  * 金石日志分析
  * Kafka Topic: log-topic
  *
  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/11 下午9:22.
  */
object LogAnalyser {

  def main(args: Array[String]) {
    var masterUrl = "local[1]"
    if (args.length > 0) {
      masterUrl = args(0)
    }

    // Create a StreamingContext with the given master URL
    val conf = new SparkConf().setMaster(masterUrl).setAppName("LogAnalyser")
    val ssc = new StreamingContext(conf, Seconds(5))

    // Kafka configurations
    val topics = Set("log-topic")
    val brokers = "localhost:9092, localhost:9093, localhost:9094"
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder")

    // Create a direct stream
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)

    // 处理log日志
    val logRequest = kafkaStream.flatMap(line => {
        Some(line._2)
      }
    )

    logRequest.print()
    // 开始流处理
    ssc.start()
    ssc.awaitTermination()
  }
}
