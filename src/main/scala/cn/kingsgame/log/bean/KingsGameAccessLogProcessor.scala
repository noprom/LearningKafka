package cn.kingsgame.log.bean

import java.util.regex.Pattern

import org.apache.log4j.Logger
import org.json.JSONObject

import scala.collection.mutable.Map

/**
  * 金石日志访问实体
  *
  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/15 下午7:52.
  */

/**
  * 客户端设别信息
  *
  * @param slient
  * @param offerId
  * @param manuFacturer
  * @param resolution
  * @param net
  * @param lang
  * @param unkownSource
  * @param androidId
  * @param time
  * @param mc
  * @param mem
  * @param sdk
  * @param vcode
  * @param app
  * @param os
  * @param apis
  * @param sNation
  * @param imei
  * @param zavj8p
  * @param cpu
  * @param versionName
  * @param board
  * @param nation
  * @param operator
  * @param product
  * @param deviceMD5
  * @param producer
  * @param brand
  * @param imsi
  * @param uuid
  * @param group
  * @param channel
  */
class Device(slient: String, offerId: String, manuFacturer: String, resolution: String,
             net: String, lang: String, unkownSource: String, androidId: String,
             time: String,
             mc: String,
             mem: String, sdk: String, vcode: String, app: String, os: String,
             apis: String,
             sNation: String, imei: String, zavj8p: String, cpu: String,
             versionName: String, board: String, nation: String, operator: String,
             product: String, deviceMD5: String, producer: String, ears: String, brand: String,
             imsi: String, uuid: String, group: String, channel: String) {
  override def toString: String = {
    "slient: " + slient + "\n" + "offerId: " + offerId + "\n" + "manuFacturer: " + manuFacturer + "\n" +
      "resolution:" + resolution + "\n" + "net: " + net + "\n" + "lang: " + lang + "\n" +
      "unkownSource: " + unkownSource + "\n" + "androidId: " + androidId + "\n" + "time: " + time + "\n" +
      "mc: " + mc + "\n" + "mem: " + "\n" + mem + "sdk: " + "\n" + sdk + "\n" + "vcode: " + vcode + "\n" +
      "app: " + app + "\n" + "os: " + "\n" + os + "apis: " + "\n" + apis + "\n" + "sNation: " + sNation + "\n" +
      "imei: " + imei + "\n" + "zavj8p: " + "\n" + zavj8p + "cpu: " + "\n" + cpu + "\n" + "versionName: " + versionName + "\n" +
      "board: " + board + "\n" + "nation: " + "\n" + nation + "operator: " + "\n" + operator + "\n" + "product: " + product + "\n" +
      "deviceMD5: " + deviceMD5 + "\n" + "producer: " + "\n" + producer + "ears: " + "\n" + ears + "\n" + "brand: " + brand + "\n" +
      "imsi: " + imsi + "\n" + "uuid: " + "\n" + uuid + "group: " + "\n" + group + "\n" + "channel: " + channel + "\n"
  }
}

/**
  * 请求里面第二个内容
  *
  * @param app
  * @param imei
  * @param mc
  * @param model
  * @param net
  * @param api
  * @param vcode
  * @param channel
  * @param deviceMd5
  * @param uuid
  * @param imsi
  * @param uid
  * @param resolution
  * @param producer
  * @param group
  * @param androidId
  */
class Info(app: String, imei: String, mc: String, model: String, net: String, api: String, vcode: String,
           channel: String, deviceMd5: String, uuid: String, imsi: String, uid: String,
           resolution: String, producer: String, group: String, androidId: String) {
  override def toString: String = {
    "app: " + app + "\n" + "imei: " + imei + "\n" + "mc: " + mc + "\n" +
      "model: " + model + "\n" + "net: " + net + "\n" + "api: " + api + "\n" +
      "vcode: " + vcode + "\n" + "channel: " + channel + "\n" + "deviceMd5: " + deviceMd5 + "\n" +
      "uuid: " + uuid + "\n" + "imsi: " + imsi + "\n" + "uid: " + uid + "\n" + "resolution: " + resolution + "\n" +
      "producer: " + producer + "\n" + "group: " + group + "\n" + "androidId: " + androidId + "\n"
  }
}

class KingsGameAccessLog(ip: String, time: String, domain: String, method: String, request: String,
                         protocol: String, status: String, size: String,
                         //device: String,
                         device: Device,
                         browser: String,
                         //info: String
                         info: Info
                        ) {
  override def toString: String = {
    "ip: " + ip + "\n" + "time: " + time + "\n" + "domain: " + domain + "\n" +
      "method:" + method + "\n" +
      "request: " + request + "\n" + "protocol: " + protocol + "\n" +
      "status: " + status + "\n" + "size: " + size + "\n" + "device: " + device + "\n" +
      "browser: " + browser + "\n" + "info: " + info
  }
}

class KingsGameAccessLogProcessor {

  val logger = Logger.getLogger(this.getClass)

  val LOG_ENTRY_PATTERN =
    """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] "(\S+)" "(\S+) (\S+) (\S+)" "-" (\d{3}) (\d+) (.+) "(.+)" "(\S+)" "(\S+)" "(\S+)" "(\S+)" "(\S+)""""

  /**
    * 解析日志
    *
    * @param logLine
    * @return
    */
  def parseLogLine(logLine: String): KingsGameAccessLog = {
    val line = logLine.replaceAll("\\\\x22", "")
    // 正则表达式提取日志字段
    val LOG_ENTRY_PATTERN =
      """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] "(\S+)" "(\S+) (\S+) (\S+)" "-" (\d{3}) (\d+) (.+) "(.+)" "(\S+)" "(\S+)" "(\S+)" "(\S+)" "(\S+)""""

    val PATTERN = Pattern.compile(LOG_ENTRY_PATTERN)
    val matcher = PATTERN.matcher(line)
    // 匹配
    if (!matcher.find()) {
      System.out.println("Cannot parse line " + line)
    }

    val ip = matcher.group(1)
    val time = matcher.group(4)
    val domain = matcher.group(5)
    val method = matcher.group(6)
    val request = matcher.group(7)
    val protocol = matcher.group(8)
    val status = matcher.group(9)
    val size = matcher.group(10)
    val deviceStr = matcher.group(11)
    val device = parseToDevice(deviceStr)
    // println(device)
    val browser = matcher.group(12)
    val infoStr = matcher.group(14)
    var info = parseToInfo(infoStr)
    // println(parseToInfo(info))
    //    var res = matcher.group(1) + "\n" + matcher.group(2) + "\n" + matcher.group(3) +
    //      "\n" + matcher.group(4) + "\n" + matcher.group(5) + "\n" + matcher.group(6) +
    //      matcher.group(7) + "\n" + matcher.group(8) + "\n" + matcher.group(9) + "\n" +
    //      matcher.group(10) + "\n" + matcher.group(11) + "\n" + matcher.group(12) + "\n" +
    //      matcher.group(13) + "\n" + matcher.group(14) + "\n" + matcher.group(15) + "\n" +
    //      matcher.group(16) + "\n" + matcher.group(17)
    //    //    println(res)

    new KingsGameAccessLog(ip, time, domain, method, request, protocol, status, size, device, browser, info)
  }

  /**
    * 将字符串转化为设备信息对象
    *
    * @param line
    * @return
    */
  def parseToDevice(line: String): Device = {
    var subline = line.substring(1, line.length - 1)
    var lineArr = subline.split(",")
    val deviceMap: Map[String, String] = Map()
    // 填充map
    for (str <- lineArr) {
      //println(str)
      val subStr = str.split(":")
      val key = subStr(0)
      var value = ""
      if (key.equals("mc")) {
        value = subStr(1) + ":" + subStr(2) + ":" + subStr(3) + ":" + subStr(4) + ":" + subStr(5) + ":" + subStr(6)
      } else if (key.equals("apis")) {
        value = subStr(1) + ":" + subStr(2) + ":" + subStr(3)
      } else {
        value = subStr(1)
      }
      deviceMap += (key -> value)
    }
    // 返回转化之后的结果
    new Device(slient = deviceMap.getOrElse("silent", "0"), offerId = deviceMap.getOrElse("offer_id", "0"),
      manuFacturer = deviceMap("manuFacturer"), resolution = deviceMap("resolution"),
      net = deviceMap("net"), lang = deviceMap("lang"), unkownSource = deviceMap("unkown_source"),
      androidId = deviceMap("androidid"), time = deviceMap("time"), mc = deviceMap("mc"),
      mem = deviceMap("mem"), sdk = deviceMap("sdk"), vcode = deviceMap("vcode"),
      app = deviceMap("app"), os = deviceMap("os"), apis = deviceMap("apis"), zavj8p = deviceMap.getOrElse("zavj8p", "0"),
      sNation = deviceMap("s_nation"), imei = deviceMap("imei"), cpu = deviceMap("cpu"),
      versionName = deviceMap("versionName"), board = deviceMap("board"), nation = deviceMap("nation"),
      operator = deviceMap("operator"), product = deviceMap("product"), deviceMD5 = deviceMap("device-md5"),
      producer = deviceMap("producer"), ears = deviceMap("ears"), brand = deviceMap("brand"),
      imsi = deviceMap("imsi"), uuid = deviceMap("uuid"), group = deviceMap("group"),
      channel = deviceMap("channel"))
  }

  /**
    * 将字符串格式化为Info类
    *
    * @param line
    * @return
    */
  def parseToInfo(line: String): Info = {
    var lineArr = line.split("&")
    val infoMap: Map[String, String] = Map()
    // 填充map
    for (str <- lineArr) {
      //println(str)
      val subStr = str.split("=")
      val key = subStr(0)
      var value = subStr(1)
      infoMap += (key -> value)
    }
    // 返回转化之后的结果
    new Info(app = infoMap("app"), imei = infoMap("imei"), mc = infoMap("mc"), model = infoMap("model"), net = infoMap("net"),
      api = infoMap("api"), vcode = infoMap("vcode"), channel = infoMap("channel"), deviceMd5 = infoMap("device_md5"),
      uuid = infoMap("uuid"), imsi = infoMap("imsi"), uid = infoMap("uid"), resolution = infoMap("resolution"),
      producer = infoMap("producer"), group = infoMap("group"), androidId = infoMap("androidid"))
  }

  /**
    * 判断该条log是否合法
    *
    * @param logLine
    * @return
    */
  def legalLogLine(logLine: String): Boolean = {
    val PATTERN = Pattern.compile(LOG_ENTRY_PATTERN)
    val matcher = PATTERN.matcher(logLine)
    matcher.find()
  }

//  def main(args: Array[String]) {
//    val logLine = "190.8.226.114 - - [29/Feb/2016:22:57:25 +0800] \"event.apiv8.com\" \"POST /event.php HTTP/1.1\" \"-\" 200 44 {\\x22manuFacturer\\x22:\\x22Z3\\x22,\\x22resolution\\x22:\\x22540x960\\x22,\\x22net\\x22:\\x22wifi\\x22,\\x22lang\\x22:\\x22es\\x22,\\x22unkown_source\\x22:\\x221\\x22,\\x22androidid\\x22:\\x2210beb6538baac29c\\x22,\\x22time\\x22:\\x221456754280\\x22,\\x22mc\\x22:\\x2200:1c:b0:68:f2:db\\x22,\\x22mem\\x22:\\x221048576\\x22,\\x22sdk\\x22:\\x2219\\x22,\\x22vcode\\x22:\\x221\\x22,\\x22app\\x22:\\x22DollarGetter_lg2\\x22,\\x22os\\x22:\\x221\\x22,\\x22apis\\x22:\\x22F:SP_V:1\\x22,\\x22s_nation\\x22:\\x22co\\x22,\\x22imei\\x22:\\x22355352911228555\\x22,\\x22cpu\\x22:\\x221203000\\x22,\\x22versionName\\x22:\\x221.0\\x22,\\x22board\\x22:\\x22yuanda72_cwet_kk\\x22,\\x22nation\\x22:\\x22ES\\x22,\\x22operator\\x22:\\x22COMCEL\\x22,\\x22product\\x22:\\x22W1\\x22,\\x22device-md5\\x22:\\x22none\\x22,\\x22producer\\x22:\\x22Z3\\x22,\\x22ears\\x22:1,\\x22brand\\x22:\\x22Z3\\x22,\\x22imsi\\x22:\\x22732101174047086\\x22,\\x22uuid\\x22:\\x22d6649f0a-1c98-4a26-acbe-6e8a94aa6060\\x22,\\x22group\\x22:\\x22km\\x22,\\x22channel\\x22:\\x22kingpin02\\x22} \"Mozilla/5.0 (Linux; U; Android 4.4.4; es-es; W1 Build/KOT49H) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1\" \"190.8.226.114\" \"app=DollarGetter_lg2&imei=355352911228555&mc=00:1c:b0:68:f2:db&model=-&net=wifi&api=-&vcode=1&channel=kingpin02&device_md5=none&uuid=d6649f0a-1c98-4a26-acbe-6e8a94aa6060&imsi=732101174047086&uid=-&resolution=540x960&producer=Z3&group=km&androidid=10beb6538baac29c\" \"0.010\" \"0.001\" \"200\""
//    val res = parseLogLine(logLine)
//    //println(res)
//  }
}