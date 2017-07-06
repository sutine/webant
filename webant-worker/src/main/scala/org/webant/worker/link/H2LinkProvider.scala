package org.webant.worker.link

import java.util

import org.apache.commons.dbutils.handlers._
import org.apache.commons.lang3.reflect.FieldUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.Link
import org.webant.commons.link.JdbcLinkProvider
import org.webant.commons.utils.BeanUtils

import scala.collection.JavaConverters._

class H2LinkProvider extends JdbcLinkProvider {
  private val logger = LogManager.getLogger(classOf[H2LinkProvider])

  DRIVER = "org.h2.Driver"

  //    val url = "jdbc:h2:tcp://localhost/~/webant;MODE=MYSQL"
  //    url = "jdbc:h2:./data/h2/webant;MODE=MYSQL"
  //    user = "webant"
  //    password = "webant"

  def init(): Boolean = {
    val params = new util.HashMap[String, Object]()
    params.put("url", "jdbc:h2:./data/h2/webant;MODE=MYSQL")
    params.put("username", "webant")
    params.put("password", "webant")

    init(params)
  }

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!super.init(params)) {
      logger.error(s"init ${getClass.getSimpleName} failed!")
      return false
    }

    logger.info(s"init ${getClass.getSimpleName} success!")
    createTable()
  }

  override def read(): Iterable[Link] = {
    getLinksToCrawl(Link.LINK_STATUS_INIT, batch)
  }

  private def getLinksToCrawl(status: String, size: Int): Iterable[Link] = {
    val sql = "SELECT id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, dataCreateTime, " +
      s"dataUpdateTime, dataDeleteTime FROM $table WHERE status = ? LIMIT ?, ?"

    val offset: Integer = 0
    val pageSize: Integer = if (size <= 0 || size > 1000) 1000 else size
    val selectParams = Array[Object](status, offset, pageSize)
    var links = Iterable.empty[Link]

    conn.setAutoCommit(false)
    try {
      links = runner.query(conn, sql, new BeanListHandler[Link](classOf[Link]), selectParams: _*).asScala
      if (links.nonEmpty) {
        val pending = links.map(link => {
          link.setStatus(Link.LINK_STATUS_PENDING)
          link
        })

        update(pending)
      }

      conn.commit()
    } catch {
      case e: Exception =>
        conn.rollback()
        e.printStackTrace()
    } finally conn.setAutoCommit(true)

    links
  }

  override def write(link: Link): Int = {
    require(conn != null)
    if (link == null) return 0
    upsert(link)
  }

  override def write(links: Iterable[Link]): Int = {
    require(conn != null)
    if (links == null || links.isEmpty) return 0
    upsert(links)
  }

  override def upsert(link: Link): Int = {
    // no reflection, simple and fast
    val sql = s"insert into $table ( id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, dataCreateTime, " +
      "dataUpdateTime, dataDeleteTime ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE " +
      "taskId = ?, siteId = ?, url = ?, referer = ?, priority = ?, lastCrawlTime = ?, status = ?, dataVersion = dataVersion + 1, dataUpdateTime = now()"
    val values = Array[Object](
      link.getId, link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime,
      link.getStatus, link.getDataVersion, link.getDataCreateTime, link.getDataUpdateTime, link.getDataDeleteTime,

      link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime, link.getStatus
    )
    runner.update(conn, sql, values: _*)
  }

  override def upsert(links: Iterable[Link]): Int = {
    if (links == null || links.isEmpty) return 0

    // no reflection, simple and fast
    val placeholders = links.toArray.map(_ => "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )").mkString(", ")
    val sql = s"insert into $table (id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, " +
      s"dataCreateTime, dataUpdateTime, dataDeleteTime) values $placeholders ON DUPLICATE KEY UPDATE " +
      //      "priority = values(priority), lastCrawlTime = values(lastCrawlTime), status = values(status), " +
      "dataVersion = dataVersion + 1, dataUpdateTime = now()"

    val values = links.toArray.flatMap(link => Array(link.getId, link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime,
      link.getStatus, link.getDataVersion, link.getDataCreateTime, link.getDataUpdateTime, link.getDataDeleteTime))
    runner.update(conn, sql, values: _*)
  }

  private def read(status: String, size: Int): Iterable[Link] = {
    val sql = s"SELECT * FROM $table WHERE status = ? ORDER by dataCreateTime desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"

    val offset: Integer = 0
    val pageSize: Integer = if (size <= 0 || size > 1000) 1000 else size
    val selectParams = Array[Object](status, offset, pageSize)

    var rs = Iterable.empty[Link]

    conn.setAutoCommit(false)
    try {
      rs = runner.query(conn, sql, new BeanListHandler[Link](classOf[Link]), selectParams: _*).asScala
      if (rs.nonEmpty) {
        val updateSql = s"update $table set status = ? where id = ?"
        val updateParams = rs.map(link => {
          Array[Object](Link.LINK_STATUS_PENDING, link.getId)
        }).toArray

        runner.batch(conn, updateSql, updateParams)
      }
      conn.commit()
    } catch {
      case e: Exception =>
        conn.rollback()
        e.printStackTrace()
    }

    rs
  }

  private def update(links: Iterable[Link]): Int = {
    // no reflection, simple and fast
    val placeholders = links.map(_ => "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )").mkString(", ")
    val sql = s"insert into $table (id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, " +
      s"dataCreateTime, dataUpdateTime, dataDeleteTime) values $placeholders ON DUPLICATE KEY UPDATE " +
      "priority = values(priority), lastCrawlTime = values(lastCrawlTime), status = values(status), " +
      "dataVersion = dataVersion + 1, dataUpdateTime = now()"

    val values = links.flatMap(link => Array(link.getId, link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime,
      link.getStatus, link.getDataVersion, link.getDataCreateTime, link.getDataUpdateTime, link.getDataDeleteTime)).toArray
    runner.update(conn, sql, values: _*)
  }

  private def merge(link: Link): Int = {
    require(conn != null)
    if (link == null) return 0
    val fieldNames = BeanUtils.getDeclaredFields(link).map(_.getName)
    val columns = fieldNames.mkString("(", ", ", ")")
    val placeholders = fieldNames.map(_ => "?").mkString("(", ",", ")")
    val values = fieldNames.map(fieldName => FieldUtils.readField(link, fieldName, true))

    val sql = s"merge into $table $columns values $placeholders"
    runner.update(conn, sql, values: _*)
  }
}
