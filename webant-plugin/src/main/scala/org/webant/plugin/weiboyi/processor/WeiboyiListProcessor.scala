package org.webant.plugin.weiboyi.processor

import com.google.gson.{GsonBuilder, JsonParser}
import org.webant.commons.utils.DateFormatUtils
import org.webant.plugin.weiboyi.data.WeiboyiDetailData
import org.webant.worker.processor.JsonPageProcessor

class WeiboyiListProcessor extends JsonPageProcessor[WeiboyiDetailData] {
  regex = "http://chuanbo.weiboyi.com/hworder/weixin/filterlist/source/all\\?start=\\d*&limit=\\d*"

  private val gson = new GsonBuilder().setDateFormat(DateFormatUtils.DATE_TIME_FORMAT).create()

  override protected def list(): Iterable[WeiboyiDetailData] = {
    val parser = new JsonParser
    val result = parser.parse(content).getAsJsonObject
    if (!result.has("data"))
      return Iterable.empty
    val data = result.getAsJsonObject("data")
    if (!data.has("rows"))
      return Iterable.empty

    val rows = data.get("rows").getAsJsonArray
    val list = (0 until rows.size()).map(index => {
      val item = rows.get(index).getAsJsonObject
      val srcId = item.get("id").getAsLong
      val json = item.get("cells").toString
      val data = gson.fromJson(json, classOf[WeiboyiDetailData])
      data.id = srcId.toString
      data.srcId = srcId.toString
      data.source = "weiboyi.com"
      data
    })
    list
  }
}
