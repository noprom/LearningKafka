package cn.kingsgame.analyser

import cn.kingsgame.log.bean.{KingsGameAccessLog, KingsGameAccessLogProcessor}
import kafka.serializer.StringDecoder
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * LogAnalyser
  * 金石日志分析
  * Kafka Topic: log-topic
  *
  *
  * # 提交spark job
./bin/spark-submit --class cn.kingsgame.analyser.KingsGameLogAnalyser \
    --master spark://nopromdeMacBook-Pro.local:7077 \
    --executor-memory 1G --total-executor-cores 2 \
    --packages "org.apache.spark:spark-streaming-kafka_2.10:1.6.0,redis.clients:jedis:2.8.0" \
    Users/noprom/Documents/Dev/Kafka/Pro/LearningKafka/out/artifacts/KingsGameLogAnalyser_jar/KingsGameLogAnalyser.jar

  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/11 下午9:22.
  */
object KingsGameLogAnalyser {

  val logger = Logger.getLogger(getClass)
  val baseSavePath = "/Users/noprom/Desktop" //暂时保存到桌面

  /**
    * 累加求和的transform
    */
  val computeRunningSum = (values: Seq[Long], state: Option[Long]) => {
    val currentCount = values.foldLeft(0L)(_ + _)
    val previousCount = state.getOrElse(0L)
    Some(currentCount + previousCount)
  }

  def main(args: Array[String]) {
    var masterUrl = "local[1]"
    if (args.length > 0) {
      masterUrl = args(0)
    }

    // 创建一个 StreamingContext
    val conf = new SparkConf().setMaster(masterUrl).setAppName("KingsGameLogAnalyser")
    val ssc = new StreamingContext(conf, Seconds(5))

    // 使用 updateStateByKey 需要设置 Checkpoint
    ssc.checkpoint("/tmp/kings-game-log-analyser-streaming-total-scala")

    // 配置 Kafka
    val topics = Set("log-topic")
    val brokers = "localhost:9092, localhost:9093, localhost:9094"
    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder")

    // 由 Kafka 创建一个 stream
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)

    // 处理log日志
    val logProcessor = new KingsGameAccessLogProcessor()
    val accessLogsDStream = kafkaStream
      .flatMap(line => Some(line._2))
      .map(logProcessor.parseLogLine).cache()

    // 处理流数据
    execTransform(accessLogsDStream, ssc)

    // 开始流处理
    ssc.start()
    ssc.awaitTermination()
  }

  /**
    * 处理流数据
    *
    * @param accessLogsDStream
    * @param ssc
    */
  def execTransform(accessLogsDStream: DStream[KingsGameAccessLog], ssc: StreamingContext): Unit = {
    // 统计不同国家的用户量
    val countryData = accessLogsDStream
      .map(x => (x.device.mCountry, 1L))
      .reduceByKey(_ + _)

    // 累计求和
    val cumulativeCountryDataCountDStream = countryData
      .updateStateByKey(computeRunningSum)
    cumulativeCountryDataCountDStream.foreachRDD(rdd => {
      // 保存到文件
      rdd.saveAsTextFile(baseSavePath + "/countryData.txt")
      rdd.foreachPartition(partionOfRecords => {
        partionOfRecords.foreach(pair => {
          val country = pair._1
          val count = pair._2
          println("----->" + country + "\t" + count)
        })
      })
    })

    // 统计不同的手机版本
    val osTypeData = accessLogsDStream
      .map(x => (x.device.mOS, 1L))
      .reduceByKey(_ + _)

    // 累计求和
    val cumulativeOSDataCountDStream = osTypeData
      .updateStateByKey(computeRunningSum)
    cumulativeOSDataCountDStream.foreachRDD(rdd => {
      // 保存到文件
      rdd.saveAsTextFile(baseSavePath + "/osTypeData.txt")
      rdd.foreachPartition(partionOfRecords => {
        partionOfRecords.foreach(pair => {
          val os = pair._1
          val count = pair._2
          println("----->" + os + "\t" + count)
        })
      })
    })

    // 统计不同的手机分辨率
    val resolutionData = accessLogsDStream
      .map(x => (x.device.mResolution, 1L))
      .reduceByKey(_ + _)

    // 累计求和
    val cumulativeResolutionDataCountDStream = resolutionData
      .updateStateByKey(computeRunningSum)
    cumulativeResolutionDataCountDStream.foreachRDD(rdd => {
      // 保存到文件
      rdd.saveAsTextFile(baseSavePath + "/resolutionData.txt")
      rdd.foreachPartition(partionOfRecords => {
        partionOfRecords.foreach(pair => {
          val mResolution = pair._1
          val count = pair._2
          println("----->" + mResolution + "\t" + count)
        })
      })
    })
  }

  //----------------------------------下面代码为测试代码-------------------------------------//

  /**
    * 执行所有转换
    *
    * @param dStream
    * @param streamCtx
    */
  def executeTransformations(dStream: DStream[(String, String)], streamCtx: StreamingContext): Unit = {

    println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||")
    // 统计不同的ip1以及访问次数
    val ip1 = dStream.filter(x => x._1.equals("ip1")).map(x => (x._2, 1)).reduceByKey(_ + _).print()
    println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||")
    //
    //    // Start - Print all attributes of the Apache Access Log
    //    printLogValues(dStream, streamCtx)
    //    // End - Print all attributes of the Apache Access Log
    //
    //    // Count the get count
    //    dStream.filter(x => x._1.equals("method") && x._2.contains("GET")).count().print()
    //
    //    // Count different urls and their count
    //    val newStream = dStream.filter(x => x._1.contains("method")).map(x => (x._2, 1))
    //    newStream.reduceByKey(_ + _).print(100)
    //
    //    // Transform the data stream
    //    val functionTransformRequestType = (rdd: RDD[(String, String)]) => {
    //      rdd.filter(f => f._1.contains("method")).map(x => (x._2, 1)).reduceByKey(_ + _)
    //    }
    //
    //    val transformedRdd = dStream.transform(functionTransformRequestType)
    //
    //    // How the state of a key should be updated
    //    val functionTotalCount = (values: Seq[Int], state: Option[Int]) => {
    //      Option(values.sum + state.sum)
    //    }
    //    streamCtx.checkpoint("checkpointDir")
    //    transformedRdd.updateStateByKey(functionTotalCount).print(100)
    //
    //    //Start - Windowing Operation
    //    executeWindowingOperations(dStream, streamCtx)
    //    //End - Windowing Operation
  }

  /**
    * 执行窗口操作
    *
    * @param dStream
    * @param streamCtx
    */
  def executeWindowingOperations(dStream: DStream[(String, String)], streamCtx: StreamingContext): Unit = {
    //This Provide the Aggregated Count of all response Codes
    println("Printing count of Response Code using windowing Operation")
    val wStream = dStream.window(Seconds(40), Seconds(20))
    val respCodeStream = wStream.filter(x => x._1.contains("respCode")).map(x => (x._2, 1))
    respCodeStream.reduceByKey(_ + _).print(100)

    //This provide the Aggregated count of all response Codes by using
    //WIndow operation in Reduce method
    println("Printing count of Response Code using reducebyKeyAndWindow Operation")
    val respCodeStream_1 = dStream.filter(x => x._1.contains("respCode")).map(x => (x._2, 1))
    respCodeStream_1.reduceByKeyAndWindow((x: Int, y: Int) => x + y, Seconds(40), Seconds(20)).print(100)

    //This will apply and print groupByKeyAndWindow in the Sliding Window
    println("Applying and Printing groupByKeyAndWindow in a Sliding Window")
    val respCodeStream_2 = dStream.filter(x => x._1.contains("respCode")).map(x => (x._2, 1))
    respCodeStream_2.groupByKeyAndWindow(Seconds(40), Seconds(20)).print(100)
  }

  /**
    * 打印rdd的值
    *
    * @param stream
    * @param streamCtx
    */
  def printLogValues(stream: DStream[(String, String)], streamCtx: StreamingContext): Unit = {
    // Implementing ForEach function for printing all the data in provided DStream
    stream.foreachRDD(foreachFunc)
    def foreachFunc = (rdd: RDD[(String, String)]) => {
      // collect() method fetches the data from all partitions and "collects" at driver node.
      // So in case data is too huge than driver may crash.
      // In production environments we persist this RDD data into HDFS or use the rdd.take(n) method.
      val array = rdd.collect()
      println("---------Start Printing Results----------")
      for (dataMap <- array.array) {
        print(dataMap._1, "-----", dataMap._2)
      }
      println("---------Finished Printing Results----------")
    }
  }
}