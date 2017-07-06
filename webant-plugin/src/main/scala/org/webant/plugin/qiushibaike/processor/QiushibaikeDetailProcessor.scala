package org.webant.plugin.qiushibaike.processor

import java.util.Date

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.plugin.fun.data.FunData
import org.webant.worker.processor.HtmlPageProcessor

class QiushibaikeDetailProcessor extends HtmlPageProcessor[FunData] {
  private val logger = LogManager.getLogger(classOf[QiushibaikeDetailProcessor])

  regex = "https://www.qiushibaike.com/article/\\d*"

  override def data(): FunData = {
    val detail = new FunData
    val srcId = StringUtils.substringAfter(url.toExternalForm, "/article/")

    require(StringUtils.isNotBlank(srcId))

    val content = doc.select("#single-next-link .content").text()
    val profileUrl = doc.select(s"#qiushi_tag_$srcId a").first().absUrl("href")
    val avatarUrl = doc.select(s"#qiushi_tag_$srcId img").first().absUrl("src")
    val userName = doc.select(s"#qiushi_tag_$srcId img").first().attr("alt")
//    val likeNum = doc.select(".stats-vote .number").text()
    val commentNum = doc.select(".stats-comments .number").text()
    val voteUp = doc.select(s"#vote-up-$srcId .number").text()
    val voteDown = doc.select(s"#vote-dn-$srcId .number").text()

    detail.profileUrl = profileUrl
    detail.avatarUrl = avatarUrl
    detail.userName = userName
//    val publishTime = doc.select(".j-list-user .u-txt .u-time").text().trim
//    detail.publishTime = DateUtils.parseDate(publishTime, DateFormatUtils.DATE_TIME_FORMAT)
    detail.content = content

    try {
      detail.likeNum = if (StringUtils.isNotBlank(voteUp)) voteUp.trim.toInt else 0
    } catch {
      case e: Exception =>
        detail.likeNum = 0
        logger.error(e.getMessage)
    }

    try {
      detail.hateNum = if (StringUtils.isNotBlank(voteDown)) Math.abs(voteDown.trim.toInt) else 0
    } catch {
      case e: Exception =>
        detail.hateNum = 0
        logger.error(e.getMessage)
    }

    try {
      detail.commentNum = if (StringUtils.isNotBlank(commentNum)) commentNum.trim.toInt else 0
    } catch {
      case e: Exception =>
        detail.commentNum = 0
        logger.error(e.getMessage)
    }

    if (!doc.select("#single-next-link img").isEmpty) {
      detail.imgUrl = doc.select("#single-next-link img").first().absUrl("src")
      detail.title = doc.select("#single-next-link img").first().attr("alt")
      detail.funType = "image"
    }
    else {
      detail.title = if (StringUtils.isNotBlank(detail.content) && detail.content.length >= 32) detail.content.substring(0, 31) + "..." else ""
      detail.funType = "text"
    }

    detail.source = "qiushibaike.com"
    detail.srcId = srcId
    detail.crawlTime = new Date

    detail
  }
}
