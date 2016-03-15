package com.huntdreams.appevent.spark

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * UserClickCountAnalytics
  * 分析用户点击
  *
  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/10 下午9:12.
  */
object UserClickCountAnalytics {

  def main(args: Array[String]): Unit = {
    var masterUrl = "local[1]"
    if (args.length > 0) {
      masterUrl = args(0)
    }

    // Create a StreamingContext with the given master URL
    val conf = new SparkConf().setMaster(masterUrl).setAppName("UserClickCountStat")
    val ssc = new StreamingContext(conf, Seconds(5))

    // Kafka configurations
    // val topics = Set("user_events")
    val topics = Set("kafkatopic")
    val brokers = "localhost:9092, localhost:9093, localhost:9094"
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder")
    val dbIndex = 1
    val clickHashKey = "app::users::click"

    // Create a direct stream
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)

    // 处理log日志
    val logRequest = kafkaStream.flatMap(line => {
        Some(line._2)
      }
    )

    logRequest.print()

//    val events = kafkaStream.flatMap(line => {
//      val data = new JSONObject(line._2)
//      Some(data)
//    })
//
//    // Compute user click times
//    val userClicks = events.map(x => (
//      x.getString("uid"), x.getInt("click_count"))
//    ).reduceByKey(_ + _)
//
//    userClicks.foreachRDD(rdd => {
//      rdd.foreachPartition(partitionOfRecords => {
//        partitionOfRecords.foreach(pair => {
//          val uid = pair._1
//          val clickCount = pair._2
//
//          println("------ " + uid + ":" + clickCount)
//          // 将结果保存到redis
//          val jedis = RedisClient.pool.getResource
//          jedis.select(dbIndex)
//          jedis.hincrBy(clickHashKey, uid, clickCount)
//          RedisClient.pool.returnResource(jedis)
//        })
//      })
//    })

    ssc.start()
    ssc.awaitTermination()
  }
}