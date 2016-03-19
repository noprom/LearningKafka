package cn.kingsgame.analyser

import java.util.regex.{Matcher, Pattern}

import cn.kingsgame.log.bean.KingsGameAccessLogProcessor
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.RDD

/**
  * LogProcessor
  * 单机版处理log日志建模
  * 直接打开idea运行即可
  *
  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/11 下午10:24.
  */
object LogProcessorApp {

  // 更目录
  private val bathPath = this.getClass.getResource("/").getPath

  /**
    * 转换日志数据
    *
    * @param logLine
    * @return
    */
  def transformLogData(logLine: String): Map[String, String] = {
    val line = logLine.replaceAll("\\\\x22", "")
    // 正则表达式提取日志字段
    val LOG_ENTRY_PATTERN =
      """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] "(\S+)" "(\S+) (\S+) (\S+)" "-" (\d{3}) (\d+) (.+) "(.+)" "(\S+)" "(\S+)" "(\S+)" "(\S+)" "(\S+)""""

    val PATTERN = Pattern.compile(LOG_ENTRY_PATTERN)
    val matcher = PATTERN.matcher(line)
    // 匹配
    if (!matcher.find()) {
      System.out.println("Cannot parse logline" + logLine)
    }
    // 返回匹配结果
    createLogDataMap(matcher)
  }

  /**
    * 创建数据对应关系
    *
    * @param m
    * @return
    */
  def createLogDataMap(m: Matcher): Map[String, String] = {
    return Map[String, String](
      ("ip1" -> m.group(1)),
      ("client" -> m.group(2)),
      ("user" -> m.group(3)),
      ("date" -> m.group(4)),
      ("domain" -> m.group(5)),
      ("method" -> m.group(6)),
      ("protocol" -> m.group(7)),
      ("respCode" -> m.group(8)),
      ("size" -> m.group(9)),
      ("body1" -> m.group(10)),
      ("browser" -> m.group(11)),
      ("ip2" -> m.group(12)),
      ("body2" -> m.group(13))
    )
  }

  /**
    * Log日志样例处理
    * 测试正则匹配
    */
  def sampleLogProcess(): Unit = {
    val logLine = "31.215.117.191 - - [29/Feb/2016:22:57:25 +0800] \"event.apiv8.com\" \"POST /event.php HTTP/1.1\" \"-\" 200 44 {silent:0,offer_id:81286,manuFacturer:GFIVE,resolution:480x800,net:wifi,lang:en,unkown_source:1,id:1533,androidid:f8082fe07e7ea44e,time:1456757844,mc:00:08:22:03:25:4f,mem:460356,sdk:19,vcode:1,app:DollarGetter_lg2,os:1,apis:F:SP_V:1,s_nation:ae,imei:866033023197522,zavj8p:1,cpu:1001000,versionName:1.0,board:Z35,nation:US,operator:du,product:GFIVE President Smart 1,device-md5:none,producer:Smart_1,brand:GFIVE,imsi:424030202223342,uuid:3ba58ccd-e1ca-477d-a5c9-85608b734e69,group:km,channel:kingpin02} \"Mozilla/5.0 (Linux; U; Android 4.4.2; en-us; GFIVE President Smart 1 Build/KOT49H) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1\" \"31.215.117.191\" \"app=DollarGetter_lg2&imei=866033023197522&mc=00:08:22:03:25:4f&model=-&net=wifi&api=-&vcode=1&channel=kingpin02&device_md5=none&uuid=3ba58ccd-e1ca-477d-a5c9-85608b734e69&imsi=424030202223342&uid=-&resolution=480x800&producer=Smart_1&group=km&androidid=f8082fe07e7ea44e\" \"0.001\" \"0.000\" \"200\""
    val LOG_ENTRY_PATTERN =
      """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] "(\S+)" "(\S+) (\S+) (\S+)" "-" (\d{3}) (\d+) (.+) "(.+)" "(\S+)" "(\S+)" "(\S+)" "(\S+)" "(\S+)""""

    val PATTERN = Pattern.compile(LOG_ENTRY_PATTERN)
    val matcher = PATTERN.matcher(logLine)
    // 匹配
    if (!matcher.find()) {
      System.out.println("Cannot parse logline " + logLine)
    }
    // 返回匹配结果

    var res = matcher.group(1) + "\n" + matcher.group(2) + "\n" + matcher.group(3) + "\n" + matcher.group(4) + "\n" + matcher.group(5) + "\n" + matcher.group(6) + matcher.group(7) + "\n" + matcher.group(8) + "\n" + matcher.group(9) + "\n" + matcher.group(10) + "\n" + matcher.group(11) + "\n" + matcher.group(12) + "\n" + matcher.group(13) + "\n" + matcher.group(14) + "\n" + matcher.group(15) + "\n" + matcher.group(16) + "\n" + matcher.group(17)
    println(res)
  }

  def main(args: Array[String]) {
    val logFile = bathPath + "20160209.log"
    val eventFile = bathPath + "20160301_13_hk12.event"

    // Spark 相关配置
    var masterUrl = "local[1]"
    if (args.length > 0) {
      masterUrl = args(0)
    }

    // 配置 SparkConf
    val conf = new SparkConf().setAppName("LogProcessor").setMaster(masterUrl)
    val sc = new SparkContext(conf)

    // 开始处理Log日志
    val logProcessor = new KingsGameAccessLogProcessor()
    val accessLogs = sc.textFile(logFile).map(logProcessor.parseLogLine).cache()

    // 统计ip数据
    val ipData = accessLogs
      .map(x => (x.ip, 1))
      .reduceByKey(_ + _)
      .take(100)
    //println(s"""ipData -----> : ${ipData.mkString("[", ",", "]")}""")
    ipData.foreach(x => {
      println(x._1 + "\t" + x._2)
    })

    // 统计不同国家的用户量
    val countryData = accessLogs
      .map(x => (x.device.country, 1))
      .reduceByKey(_ + _)
      .take(100)
    //println(s"""countryData -----> : ${countryData.mkString("[", ",", "]")}""")
    // 打印到控制台
    countryData.foreach(x => {
      println(x._1 + "\t" + x._2)
    })

    // 打印日志内容
    // printLogVals(newLogs)

    // 统计不同的ip1以及访问次数
    //    val ip1 = newLogs.filter(x => x._1.equals("ip1")).map(x => (x._2, 1)).reduceByKey(_ + _).collect()
    //    printLogCount(ip1)
    //sampleLogProcess()
  }

  /**
    * 打印日志内容
    *
    * @param rdd
    */
  def printLogVals(rdd: RDD[(String, String)]) = {
    val array = rdd.collect()
    println("---------Start Printing Results----------")
    for (dataMap <- array.array) {
      println(dataMap._1, "----->", dataMap._2)
    }
    println("---------Finished Printing Results----------")
  }

  def printLogCount(rdd: Array[(String, Int)]) = {
    for (map <- rdd) {
      println(map)
    }
  }
}

/**
  * 64.242.88.10 - - [07/Mar/2004:16:05:49 -0800] "GET /twiki/bin/edit/Main/Double_bounce_sender?topicparent=Main.ConfigurationVariables HTTP/1.1" 401 12846
  **/