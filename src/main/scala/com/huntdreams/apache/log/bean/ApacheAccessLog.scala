package com.huntdreams.apache.log.bean

/**
  * ApacheAccessLog
  * 日志实体
  *
  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/15 下午3:57.
  */
case class ApacheAccessLog(ipAddress: String, clientIdentd: String,
                           userId: String, dateTime: String, method: String,
                           endpoint: String, protocol: String,
                           responseCode: Int, contentSize: Long) {
}

object ApacheAccessLog {
  val PATTERN = """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] "(\S+) (\S+) (\S+)" (\d{3}) (\d+)""".r

  /**
    * 解析日志
    *
    * @param log
    * @return
    */
  def parseLogLine(log: String): ApacheAccessLog = {
    val res = PATTERN.findFirstMatchIn(log)
    if (res.isEmpty) {
      throw new RuntimeException("Cannot parse log line: " + log)
    }
    val m = res.get
    ApacheAccessLog(m.group(1), m.group(2), m.group(3), m.group(4),
      m.group(5), m.group(6), m.group(7), m.group(8).toInt, m.group(9).toLong)
  }

  /**
    * 判断该条log是否合法
    *
    * @param log
    * @return
    */
  def legalLogLine(log: String): Boolean = {
    val res = PATTERN.findFirstMatchIn(log)
    !res.isEmpty
  }
}
