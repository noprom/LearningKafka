package cn.kingsgame.log.bean

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
  * @param id
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
class Device(slient: Long, offerId: String, manuFacturer: String, resolution: String,
             net: String, lang: String, unkownSource: String, id: String, androidId: String,
             time: String, mc: String, mem: String, sdk: String, vcode: String, app: String,
             os: String, apis: String, sNation: String, imei: String, zavj8p: String, cpu: String,
             versionName: String, board: String, nation: String, operator: String,
             product: String, deviceMD5: String, producer: String, brand: String,
             imsi: String, uuid: String, group: String, channel: String)

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
           resolution: String, producer: String, group: String, androidId: String)

class KingsGameAccessLog {

}

object KingsGameAccessLog {

}