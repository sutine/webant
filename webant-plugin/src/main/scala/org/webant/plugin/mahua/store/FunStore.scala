package org.webant.plugin.mahua.store

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.plugin.fun.data.FunData
import org.webant.worker.store.MysqlStore

class FunStore extends MysqlStore[FunData] {
  private val logger = LogManager.getLogger(classOf[FunStore])
  private val table = "wefun"

  override def upsert(list: Iterable[FunData]): Int = {
    require(conn != null)
    if (list == null || list.isEmpty) return 0

    val fieldNames = Array("bizId", "userName", "avatarUrl", "profileUrl", "title", "content", "publishTime", "imgUrl", "imgWith", "imgHeight", "likeNum", "hateNum", "commentNum", "funType", "source", "srcId", "srcUrl", "crawlTime", "dataVersion", "dataCreateTime", "dataUpdateTime", "dataDeleteTime")
    val columns = fieldNames.mkString("(", ",", ")")
    val placeholders = list.map(_ => fieldNames.map(_ => "?").mkString("(", ",", ")")).mkString(",")
    val sql = s"insert into $table $columns values $placeholders ON DUPLICATE KEY UPDATE dataVersion = dataVersion + 1, dataUpdateTime = now()"
    val values = list.flatMap(data => {
      val funType = if (StringUtils.isNotBlank(data.imgUrl)) "image" else "text"
      Array(data.id, data.userName, data.avatarUrl, data.profileUrl, data.title, data.content, data.publishTime, data.imgUrl, data.imgWith, data.imgHeight, data.likeNum, data.hateNum, data.commentNum, funType, data.source, data.srcId, data.srcUrl, data.crawlTime, data.dataVersion, data.dataCreateTime, data.dataUpdateTime, data.dataDeleteTime)
    }).toArray

    var affectRowCount = 0

    try {
      affectRowCount = runner.update(conn, sql, values: _*)
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
    }

    affectRowCount
  }

  override def upsert(data: FunData): Int = {
    require(conn != null)
    if (data == null) return 0

    val funType = if (StringUtils.isNotBlank(data.imgUrl)) "image" else "text"
    val fieldNames = Array("bizId", "userName", "avatarUrl", "profileUrl", "title", "content", "publishTime", "imgUrl", "imgWith", "imgHeight", "likeNum", "hateNum", "commentNum", "funType", "source", "srcId", "srcUrl", "crawlTime", "dataVersion", "dataCreateTime", "dataUpdateTime", "dataDeleteTime")
    val columns = fieldNames.mkString("(", ",", ")")
    val placeholders = fieldNames.map(_ => "?").mkString("(", ",", ")")
    val sql = s"insert into $table $columns values $placeholders ON DUPLICATE KEY UPDATE dataVersion = dataVersion + 1, dataUpdateTime = now()"
    val values = Array(data.id, data.userName, data.avatarUrl, data.profileUrl, data.title, data.content, data.publishTime, data.imgUrl, data.imgWith, data.imgHeight, data.likeNum, data.hateNum, data.commentNum, funType, data.source, data.srcId, data.srcUrl, data.crawlTime, data.dataVersion, data.dataCreateTime, data.dataUpdateTime, data.dataDeleteTime)

    var affectRowCount = 0

    try {
      affectRowCount = runner.update(conn, sql, values: _*)
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
    }

    affectRowCount
  }
}