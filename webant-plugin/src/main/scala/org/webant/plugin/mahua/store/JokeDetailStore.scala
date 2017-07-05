package org.webant.plugin.mahua.store

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.plugin.fun.data.FunData
import org.webant.worker.store.MysqlStore

class JokeDetailStore extends MysqlStore[FunData] {
  private val logger = LogManager.getLogger(classOf[JokeDetailStore])

  override def upsert(list: Iterable[FunData]): Int = {
    require(conn != null)
    if (list == null || list.isEmpty) return 0

    list.map(upsert).sum
  }

  override def upsert(data: FunData): Int = {
    require(conn != null)
    if (data == null) return 0

    var table: String = null
    var fieldNames: Array[String] = null
    var values: Array[Object] = null

    if (StringUtils.isNotBlank(data.imgUrl)) {
      table = "fun_image"
      fieldNames = Array("id", "biz_id", "image_url", "content", "name", "profile_image", "like_num", "hate_num", "source", "publish_time", "data_create_time", "data_update_time", "data_delete_time")
      values = Array(data.srcId, data.id, data.imgUrl, data.title, data.userName, data.avatarUrl, data.likeNum, data.hateNum, "mahua", data.publishTime, data.dataCreateTime, data.dataUpdateTime, data.dataDeleteTime)
    } else {
      table = "joke"
      fieldNames = Array("id", "biz_id", "title", "content", "name", "profile_image", "like_num", "hate_num", "source", "publish_time", "data_create_time", "data_update_time", "data_delete_time")
      values = Array(data.srcId, data.id, data.title, data.content, data.userName, data.avatarUrl, data.likeNum, data.hateNum, "mahua", data.publishTime, data.dataCreateTime, data.dataUpdateTime, data.dataDeleteTime)
    }

    val columns = fieldNames.mkString("(", ",", ")")
    val placeholders = fieldNames.map(_ => "?").mkString("(", ",", ")")
    val sql = s"insert into $table $columns values $placeholders ON DUPLICATE KEY UPDATE data_update_time = now()"

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