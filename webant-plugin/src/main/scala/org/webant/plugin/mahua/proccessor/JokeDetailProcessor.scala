package org.webant.plugin.mahua.proccessor

import java.util.Date

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.webant.commons.utils.DateFormatUtils
import org.webant.plugin.mahua.data.JokeDetailData
import org.webant.worker.processor.HtmlPageProcessor

class JokeDetailProcessor extends HtmlPageProcessor[JokeDetailData] {
  regex = "http://www.mahua.com/xiaohua/\\d*.htm"

  override def data(): JokeDetailData = {
    val detail = new JokeDetailData
    val id = doc.select("dl.mahua-view").first().attr("mahua")
    val jokeType = doc.select("dl.mahua-view").first().attr("joke-type")

    require(StringUtils.isNotBlank(id))
    require(StringUtils.isNotBlank(jokeType))

    detail.id = DigestUtils.md5Hex(s"mahua_$id")

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
    detail.srcId = id
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

    detail
  }
}
