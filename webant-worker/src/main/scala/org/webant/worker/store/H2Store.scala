package org.webant.worker.store

import java.util

import org.apache.log4j.LogManager
import org.webant.commons.utils.JsonUtils
import org.webant.worker.http.HttpDataEntity

class H2Store[T <: HttpDataEntity] extends JdbcStoreProvider[T] {
  private val logger = LogManager.getLogger(classOf[H2Store[HttpDataEntity]])

  DRIVER = "org.h2.Driver"

  def init(): Boolean = {
    val params = new util.HashMap[String, Object]()
    params.put("url", "jdbc:h2:./data/h2/data;MODE=MYSQL")
    params.put("username", "webant")
    params.put("password", "webant")

    init(params)
  }

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!super.init(params)) {
      logger.error("init H2Store failed!")
      return false
    }

    logger.info(s"init H2Store success!")
    createTable()
  }

  override def save(data: T): Int = {
    0
  }

  override def upsert(list: Iterable[T]): Int = {
    require(conn != null)
    if (list == null || list.isEmpty) return 0

    val fieldNames = Array("id", "data", "source", "srcId", "srcUrl", "crawlTime", "dataVersion", "dataCreateTime", "dataUpdateTime", "dataDeleteTime")
    val columns = fieldNames.mkString("(", ",", ")")
    val placeholders = list.map(_ => fieldNames.map(_ => "?").mkString("(", ",", ")")).mkString(",")
    val sql = s"insert into data $columns values $placeholders ON DUPLICATE KEY UPDATE dataVersion = dataVersion + 1, dataUpdateTime = now()"
    val values = list.flatMap(data => Array(data.id, JsonUtils.toJson(data), data.source, data.srcId, data.srcUrl, data.crawlTime, data.dataVersion, data.dataCreateTime, data.dataUpdateTime, data.dataDeleteTime)).toArray

    var affectRowCount = 0

    try {
      affectRowCount = runner.update(conn, sql, values: _*)
    } catch {
      case e: Exception =>
        logger.error(e.getMessage())
    }

    affectRowCount
  }

  override def upsert(data: T): Int = {
    require(conn != null)
    if (data == null) return 0

    val fieldNames = Array("id", "data", "source", "srcId", "srcUrl", "crawlTime", "dataVersion", "dataCreateTime", "dataUpdateTime", "dataDeleteTime")
    val columns = fieldNames.mkString("(", ",", ")")
    val placeholders = fieldNames.map(_ => "?").mkString("(", ",", ")")
    val sql = s"insert into data $columns values $placeholders ON DUPLICATE KEY UPDATE dataVersion = dataVersion + 1, dataUpdateTime = now()"
    val values = Array(data.id, JsonUtils.toJson(data), data.source, data.srcId, data.srcUrl, data.crawlTime, data.dataVersion, data.dataCreateTime, data.dataUpdateTime, data.dataDeleteTime)

    var affectRowCount = 0

    try {
      affectRowCount = runner.update(conn, sql, values: _*)
    } catch {
      case e: Exception =>
        logger.error(e.getMessage())
    }

    affectRowCount
  }
}