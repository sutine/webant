package org.webant.plugin.budejie.proccessor

import java.util.Date

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.webant.commons.utils.DateFormatUtils
import org.webant.plugin.budejie.data.BudejieDetailData
import org.webant.worker.processor.HtmlPageProcessor

class BudejieDetailProcessor extends HtmlPageProcessor[BudejieDetailData] {
  regex = "http://www.budejie.com/detail-\\d*.html"

  override def data(): BudejieDetailData = {
    val detail = new BudejieDetailData
    val id = doc.select("#hidPid").attr("value")
    var jokeType = "text"
    if (!doc.select(".j-r-list-c-img").isEmpty)
      jokeType = "image"
    else if (!doc.select(".j-video-c").isEmpty)
      jokeType = "video"
    else
      jokeType = "text"

    require(StringUtils.isNotBlank(id))
//    require(StringUtils.isNotBlank(jokeType))

    detail.id = DigestUtils.md5Hex(s"mahua_$id")

    detail.profileUrl = doc.select(".j-list-user .u-img a").attr("href").trim
    detail.avatarUrl = doc.select(".j-list-user .u-img a img").attr("data-original").trim
    detail.userName = doc.select(".j-list-user .u-txt a").text().trim
    val publishTime = doc.select(".j-list-user .u-txt .u-time").text().trim
    detail.publishTime = DateUtils.parseDate(publishTime, DateFormatUtils.DATE_TIME_FORMAT)
    detail.title = doc.select(".j-r-list-c .j-r-list-c-desc").text().trim
    detail.imgUrl = doc.select(".content-text img").attr("src").trim
    val likeNum = doc.select(".j-r-list-tool .j-r-list-tool-l-up span").text()
    val hateNum = doc.select(".j-r-list-tool .j-r-list-tool-l-down span").text()
//    detail.shareNum = doc.select(".j-r-list-tool .j-r-list-tool-ct-share-c").text().replace(" ", "")
    val comment = doc.select(".j-r-list-tool .j-r-list-tool-l-comment span").text()
    detail.likeNum = if (StringUtils.isNotBlank(likeNum)) likeNum.trim.toInt else 0
    detail.hateNum = if (StringUtils.isNotBlank(hateNum)) hateNum.trim.toInt else 0
    detail.commentNum = if (StringUtils.isNotBlank(comment)) comment.trim.toInt else 0

    detail.source = "budejie.com"
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
