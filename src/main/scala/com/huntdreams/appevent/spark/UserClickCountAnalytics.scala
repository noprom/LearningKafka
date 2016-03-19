package com.huntdreams.appevent.spark

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.json.JSONObject

/**
  * UserClickCountAnalytics
  * 分析用户点击
  *
  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/10 下午9:12.
  */
object UserClickCountAnalytics {

  /**
    * 累加求和的transform
    */
  val computeRunningSum = (values: Seq[Long], state: Option[Long]) => {
    val currentCount = values.foldLeft(0L)(_ + _)
    val previousCount = state.getOrElse(0L)
    Some(currentCount + previousCount)
  }

  def main(args: Array[String]): Unit = {
    var masterUrl = "local[1]"
    if (args.length > 0) {
      masterUrl = args(0)
    }

    // Create a StreamingContext with the given master URL
    val conf = new SparkConf().setMaster(masterUrl).setAppName("UserClickCountStat")
    val ssc = new StreamingContext(conf, Seconds(5))

    // 使用 updateStateByKey 需要设置 Checkpoint
    ssc.checkpoint("/tmp/UserClickCountAnalytics-streaming-total-scala")

    // Kafka configurations
    val topics = Set("user_events")
    //val topics = Set("kafkatopic")
    val brokers = "localhost:9092, localhost:9093, localhost:9094"
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder")
    val dbIndex = 1
    val clickHashKey = "app::users::click"

    // Create a direct stream
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)

    // 处理log日志
//    val logRequest = kafkaStream.flatMap(line => {
//        Some(line._2)
//      }
//    )
//
//    logRequest.print()

    val events = kafkaStream.flatMap(line => {
      val data = new JSONObject(line._2)
      Some(data)
    })

    // Compute user click times
    val userClicks = events.map(x => (
      x.getString("uid"), x.getLong("click_count"))
    ).reduceByKey(_ + _)

    // 每一个slide实时求和
    userClicks.foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
        partitionOfRecords.foreach(pair => {
          val uid = pair._1
          val clickCount = pair._2

          println("Real time ------> " + uid + ":" + clickCount)
          // 将结果保存到redis
          val jedis = RedisClient.pool.getResource
          jedis.select(dbIndex)
          jedis.hincrBy(clickHashKey, uid, clickCount)
          RedisClient.pool.returnResource(jedis)
        })
      })
    })

    // 累计求和
    val cumulativeUserClicksCountDStream = userClicks
      .updateStateByKey(computeRunningSum)
    cumulativeUserClicksCountDStream.foreachRDD(rdd => {
      val responseCodeToCount = rdd.take(100)
      println(s"""|||||----->>>>>cumulativeUserClicksCountDStream: ${responseCodeToCount.mkString("[", ",", "]")}""")

      rdd.foreachPartition(partionOfRecords => {
        partionOfRecords.foreach(pair => {
          val uid = pair._1
          val clickCount = pair._2
          println("Total ------> " + uid + ":" + clickCount)
        })
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }
}

/*
# 提交spark job
./bin/spark-submit --class com.huntdreams.appevent.spark.UserClickCountAnalytics \
    --master spark://nopromdeMacBook-Pro.local:7077 \
    --executor-memory 1G --total-executor-cores 2 \
    --packages "org.apache.spark:spark-streaming-kafka_2.10:1.6.0,redis.clients:jedis:2.8.0" \
    /Users/noprom/Documents/Dev/Kafka/Pro/LearningKafka/out/artifacts/UserClickCountAnalytics_jar/UserClickCountAnalytics.jar

 */