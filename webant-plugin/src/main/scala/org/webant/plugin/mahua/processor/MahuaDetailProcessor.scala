package org.webant.plugin.mahua.processor

import java.nio.charset.Charset
import java.util.Date

import com.google.gson.JsonParser
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.apache.http.client.fluent.Response
import org.webant.commons.utils.DateFormatUtils
import org.webant.plugin.fun.data.FunData
import org.webant.worker.processor.HtmlPageProcessor

class MahuaDetailProcessor extends HtmlPageProcessor[FunData] {
  regex = "http://www.mahua.com/xiaohua/\\d*.htm"

  override def data(): FunData = {
    val detail = new FunData
    val srcId = doc.select("dl.mahua-view").first().attr("mahua")
    val jokeType = doc.select("dl.mahua-view").first().attr("joke-type")

    require(StringUtils.isNotBlank(srcId))
    require(StringUtils.isNotBlank(jokeType))

    detail.avatarUrl = doc.select("dl dt a img").first().attr("src")
    detail.userName = doc.select("dl dt p.joke-uname a").text()
    detail.profileUrl = doc.select("dl dt p.joke-uname a").attr("href")
    detail.title = doc.select("dl dt h1.joke-title").text()

    val publishTime = doc.select("dl dt p.joke-uname span").text()
    detail.publishTime = DateUtils.parseDate(publishTime, DateFormatUtils.DATE_TIME_FORMAT)
    detail.likeNum = doc.select("dd.operation div.operation-btn a.ding").text().toInt
    detail.hateNum = doc.select("dd.operation div.operation-btn a.cai").text().toInt
    detail.commentNum = doc.select("dd.operation div.operation-btn a.comment").text().toInt

    detail.source = "mahua.com"
    detail.srcId = srcId
    detail.crawlTime = new Date

    if ("pic" == jokeType || "gif" == jokeType) {
      val image = doc.select("dl dd.content div.joke-content img").first()
      detail.imgUrl = image.attr("src")
      detail.imgWith = image.attr("width").toInt
      detail.imgHeight = image.attr("height").toInt
      detail.funType = "image"
    }
    else if ("text" == jokeType) {
      detail.content = doc.select("dl dd.content div.joke-content").first().text()
      detail.funType = "text"
    }

    getLikeHateCount(detail, System.currentTimeMillis())
    detail
  }

  def getLikeHateCount(detail: FunData, time: Long): FunData = {
    val url = s"http://user.mahua.com/ajax/joke/checkJokesDynamic?callback=jQuery&joke_ids=${detail.srcId}&_=$time"

    val resp: Response = org.apache.http.client.fluent.Request.Get(url)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/json;q=0.9,image/webp,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate, sdch")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("Proxy-Connection", "keep-alive")
      .addHeader("DNT", "1")
      .execute
    val content = resp.returnContent.asString(Charset.forName("UTF-8"))
    if (StringUtils.isBlank(content)) return detail

    val start = content.indexOf("(")
    val end = content.lastIndexOf(")")
    val body = StringUtils.substring(content, start + 1, end)
    if (StringUtils.isBlank(body)) return detail

    val parser = new JsonParser
    val result = parser.parse(body).getAsJsonObject
    if (!result.has("data")) return detail

    val data = result.get("data").getAsJsonObject
    detail.likeNum = data.getAsJsonObject(detail.srcId).getAsJsonObject("upDown").getAsJsonObject("count").get("up").getAsInt
    detail.hateNum = data.getAsJsonObject(detail.srcId).getAsJsonObject("upDown").getAsJsonObject("count").get("down").getAsInt
    detail.commentNum = data.getAsJsonObject(detail.srcId).getAsJsonObject("comment").get("count").getAsInt

    detail
  }

}
