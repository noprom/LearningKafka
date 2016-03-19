package cn.kingsgame.analyser

import kafka.serializer.StringDecoder
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
    val logProcessor = new LogProcessor()
    val newLogsDstream = kafkaStream.flatMap(line => logProcessor.transformLogData(line._2))

    // 处理流数据
    executeTransformations(newLogsDstream, ssc)

    // 开始流处理
    ssc.start()
    ssc.awaitTermination()
  }

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