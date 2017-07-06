package org.webant.plugin.zhihu.processor

import java.util.Date

import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import org.webant.commons.utils.JsonUtils
import org.webant.plugin.zhihu.data.AnswerDetailData
import org.webant.worker.processor.JsonPageProcessor

import scala.collection.JavaConverters._

class AnswersProcessor extends JsonPageProcessor[AnswerDetailData] {
  regex = "https://www.zhihu.com/api/v4/members/[0-9a-zA-Z-_.]*/answers?[\\w\\W]*"

  override protected def links(): Iterable[String] = {
    val parser = new JsonParser
    val result = parser.parse(content).getAsJsonObject
    if (!result.has("paging"))
      return Iterable.empty
    val paging = result.getAsJsonObject("paging")
    if (!paging.has("is_end"))
      return Iterable.empty
    val isEnd = paging.get("is_end").getAsBoolean
    if (isEnd)
      return Iterable.empty

    val url = paging.get("next").getAsString
    Iterable(url)
  }

  override protected def list(): Iterable[AnswerDetailData] = {
    val parser = new JsonParser
    val result = parser.parse(content).getAsJsonObject
    if (!result.has("data"))
      return Iterable.empty
    val data = result.get("data").toString
    val list = JsonUtils.fromJson[java.util.List[AnswerDetailData]](data, new TypeToken[java.util.List[AnswerDetailData]]() {}.getType)
    list.asScala.map(detail => {
      detail.source = "zhihu.com"
      detail.srcId = detail.id
      detail.crawlTime = new Date
      detail
    })
  }
}
