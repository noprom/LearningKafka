package com.huntdreams.apache.log.analyser

import com.huntdreams.apache.log.bean.ApacheAccessLog
import com.huntdreams.apache.log.util.OrderingUtils
import org.apache.spark.{SparkConf, SparkContext}

/**
  * LogAnalyzer
  * apache日志分析
  * The LogAnalyzer takes in an apache access log file and
  * computes some statistics on them.
  *
  * Example command to run:
  * % spark-submit
  * --class "com.databricks.apps.logs.chapter1.LogAnalyzer"
  * --master local[4]
  * target/scala-2.10/spark-logs-analyzer_2.10-1.0.jar
  * ../../data/apache.access.log
  *
  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/15 下午4:01.
  */
object LogAnalyzer {

  // 更目录
  private val bathPath = this.getClass.getResource("/").getPath

  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("Log Analyzer in Scala").setMaster("local[1]")
    val sc = new SparkContext(sparkConf)

    // 设置日志路径
    var logFile = ""
    if (args.length > 0) {
      logFile = args(0)
    } else {
      logFile = bathPath + "apache.access.log"
    }

    // 过滤非法请求
    val accessLogs = sc.textFile(logFile)
      .filter(x => ApacheAccessLog.legalLogLine(x))
      .map(ApacheAccessLog.parseLogLine).cache()

    // Calculate statistics based on the content size.
    val contentSizes = accessLogs.map(log => log.contentSize).cache()
    println("Content Size Avg: %s, Min: %s, Max: %s".format(
      contentSizes.reduce(_ + _) / contentSizes.count,
      contentSizes.min,
      contentSizes.max))

    // Compute Response Code to Count.
    val responseCodeToCount = accessLogs
      .map(log => (log.responseCode, 1))
      .reduceByKey(_ + _)
      .take(100)
    println(s"""Response code counts: ${responseCodeToCount.mkString("[", ",", "]")}""")

    // Any IPAddress that has accessed the server more than 10 times.
    val ipAddresses = accessLogs
      .map(log => (log.ipAddress, 1))
      .reduceByKey(_ + _)
      .filter(_._2 > 10)
      .map(_._1)
      .take(100)
    println(s"""IPAddresses > 10 times: ${ipAddresses.mkString("[", ",", "]")}""")

    // Top Endpoints.
    val topEndpoints = accessLogs
      .map(log => (log.endpoint, 1))
      .reduceByKey(_ + _)
      .top(10)(OrderingUtils.SecondValueOrdering)
    println(s"""Top Endpoints: ${topEndpoints.mkString("[", ",", "]")}""")

    sc.stop()
  }
}