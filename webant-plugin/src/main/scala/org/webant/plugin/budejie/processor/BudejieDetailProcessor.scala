package org.webant.plugin.budejie.processor

import java.util.Date

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.apache.log4j.LogManager
import org.webant.commons.utils.DateFormatUtils
import org.webant.plugin.fun.data.FunData
import org.webant.worker.processor.HtmlPageProcessor

class BudejieDetailProcessor extends HtmlPageProcessor[FunData] {
  private val logger = LogManager.getLogger(classOf[BudejieDetailProcessor])

  regex = "http://www.budejie.com/detail-\\d*.html"

  override def data(): FunData = {
    val detail = new FunData
    val srcId = doc.select(".j-r-list-tool").attr("data-id")
    val funType = doc.select(".j-r-list-tool").attr("data-type")

    require(StringUtils.isNotBlank(srcId))

    detail.profileUrl = doc.select(".j-list-user .u-img a").first().absUrl("href").trim
    detail.avatarUrl = doc.select(".j-list-user .u-img a img").attr("data-original").trim
    detail.userName = doc.select(".j-list-user .u-txt a").text().trim
    val publishTime = doc.select(".j-list-user .u-txt .u-time").text().trim
    detail.publishTime = DateUtils.parseDate(publishTime, DateFormatUtils.DATE_TIME_FORMAT)
    detail.content = doc.select(".j-r-list-c .j-r-list-c-desc").text().trim
    detail.title = doc.select(".j-r-list-tool").attr("data-title")
    val likeNum = doc.select(".j-r-list-tool .j-r-list-tool-l-up span").text()
    val hateNum = doc.select(".j-r-list-tool .j-r-list-tool-l-down span").text()
    val shareNum = doc.select(".j-r-list-tool .j-r-list-tool-ct-share-c span").text()
    val commentNum = doc.select(".j-r-list-tool .j-comment .comment-counts").text()

    try {
      detail.likeNum = if (StringUtils.isNotBlank(likeNum)) likeNum.trim.toInt else 0
    } catch {
      case e: Exception =>
        detail.likeNum = 0
        logger.error(e.getMessage)
    }

    try {
      detail.hateNum = if (StringUtils.isNotBlank(hateNum)) hateNum.trim.toInt else 0
    } catch {
      case e: Exception =>
        detail.hateNum = 0
        logger.error(e.getMessage)
    }

    try {
      val num = shareNum.split("\u00A0")(2)
      detail.shareNum = if (StringUtils.isNotBlank(num)) num.trim.toInt else 0
    } catch {
      case e: Exception =>
        detail.shareNum = 0
        logger.error(e.getMessage)
    }

    try {
      detail.commentNum = if (StringUtils.isNotBlank(commentNum)) commentNum.trim.toInt else 0
    } catch {
      case e: Exception =>
        detail.commentNum = 0
        logger.error(e.getMessage)
    }

    if ("10" == funType) {
      detail.imgUrl = doc.select(".j-r-list-c-img img").attr("src").trim
      detail.title = doc.select(".j-r-list-c-img img").attr("title").trim
      detail.funType = "image"
    }
    else if ("41" == funType) {
      detail.videoUrl = doc.select(".j-video-c .j-video").attr("data-mp4").trim
      detail.videoCaptureUrl = doc.select(".j-video-c .j-video").attr("data-poster").trim
      detail.videoDuration = doc.select(".j-video-c").attr("data-time").trim
      detail.funType = "video"
    }
    else if ("29" == funType) {
//      detail.title = if (StringUtils.isNotBlank(detail.content)) detail.content.substring(0, 32) + "..." else ""
      detail.funType = "text"
    }

    detail.source = "budejie.com"
    detail.srcId = srcId
    detail.crawlTime = new Date

    detail
  }
}
