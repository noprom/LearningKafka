package com.huntdreams.apache.log.util

/**
  * OrderingUtils
  * 排序工具类
  *
  * Author: Noprom <tyee.noprom@qq.com>
  * Date: 16/3/15 下午4:00.
  */
object OrderingUtils {

  object SecondValueOrdering extends Ordering[(String, Int)] {
    def compare(a: (String, Int), b: (String, Int)) = {
      a._2 compare b._2
    }
  }

  object SecondValueLongOrdering extends Ordering[(String, Long)] {
    def compare(a: (String, Long), b: (String, Long)) = {
      a._2 compare b._2
    }
  }
}