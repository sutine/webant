package org.webant.plugin.weiboyi.processor

import com.google.gson.JsonParser
import org.apache.log4j.LogManager
import org.webant.plugin.weiboyi.data.WeiboyiDetailData
import org.webant.worker.processor.JsonPageProcessor

class WeiboyiSeedProcessor extends JsonPageProcessor[WeiboyiDetailData] {
  private val logger = LogManager.getLogger(classOf[WeiboyiSeedProcessor])
  regex = "http://chuanbo.weiboyi.com/hworder/weixin/filterlist/source/all"

  protected override def links(): Iterable[String] = {
    if (!isJson(content)) {
      logger.error("no json content!")
      return Iterable.empty
    }

    val parser = new JsonParser
    val result = parser.parse(content).getAsJsonObject
    if (!result.has("data"))
      return Iterable.empty
    val data = result.getAsJsonObject("data")
    if (!data.has("total"))
      return Iterable.empty

    val size = 200
    val total = data.get("total").getAsInt
    val totalPage = Math.ceil(total.toFloat / size).toInt
    (0 until totalPage).map(index => {
      val offset = index * size
      val url = s"http://chuanbo.weiboyi.com/hworder/weixin/filterlist/source/all?start=$offset&limit=$size"
      url
    })
  }
}
