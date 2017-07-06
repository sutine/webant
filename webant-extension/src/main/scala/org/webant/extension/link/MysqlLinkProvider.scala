package org.webant.extension.link

import org.apache.commons.dbutils.handlers.BeanListHandler
import org.apache.log4j.LogManager
import org.webant.commons.entity.Link
import org.webant.commons.link.JdbcLinkProvider

import scala.collection.JavaConverters._

class MysqlLinkProvider extends JdbcLinkProvider {
  private val logger = LogManager.getLogger(classOf[MysqlLinkProvider])

  DRIVER = "com.mysql.jdbc.Driver"

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!super.init(params)) {
      logger.error(s"init ${getClass.getSimpleName} failed!")
      return false
    }

    logger.info(s"init ${getClass.getSimpleName} success!")
    createTable()
  }

  override def read(): Iterable[Link] = {
    try {
      read(Link.LINK_STATUS_INIT, batch)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        Iterable.empty
    }
  }

  override def write(link: Link): Int = {
    upsert(link)
  }

  override def write(links: Iterable[Link]): Int = {
    upsert(links)
  }

  private def read(status: String, size: Int): Iterable[Link] = {
    val sql = "SELECT id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, dataCreateTime, " +
      s"dataUpdateTime, dataDeleteTime FROM $table WHERE status = ? ORDER by dataCreateTime desc LIMIT ?, ?"

    val pageNo: Integer = 0
    val pageSize: Integer = if (size <= 0 || size > 1000) 1000 else size
    val selectParams = Array[Object](status, pageNo, pageSize)

    var links = Iterable.empty[Link]

    try {
      conn.setAutoCommit(false)
      links = runner.query(conn, sql, new BeanListHandler[Link](classOf[Link]), selectParams: _*).asScala
      if (links.nonEmpty) {
        val updateSql = s"update $table set status = ?, dataVersion = dataVersion + 1, dataUpdateTime = now() where id = ?"
        val updateParams = links.map(link => {
          Array[Object](Link.LINK_STATUS_PENDING, link.getId)
        }).toArray

        runner.batch(conn, updateSql, updateParams)
      }
      conn.commit()
    } catch {
      case e: Exception =>
        conn.rollback()
        e.printStackTrace()
    } finally
      conn.setAutoCommit(true)

    links
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
    // links.toArray.map can work, links.map can not work, it may be a bug in scala map()
    val placeholders = links.toArray.map(_ => "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )").mkString(", ")
    val sql = s"insert into $table (id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, " +
      s"dataCreateTime, dataUpdateTime, dataDeleteTime) values $placeholders ON DUPLICATE KEY UPDATE " +
      //      "priority = values(priority), lastCrawlTime = values(lastCrawlTime), status = values(status), " +
      "dataVersion = dataVersion + 1, dataUpdateTime = now()"

    val values = links.toArray.flatMap(link => Array(link.getId, link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime,
      link.getStatus, link.getDataVersion, link.getDataCreateTime, link.getDataUpdateTime, link.getDataDeleteTime))
    runner.update(conn, sql, values: _*)
  }
}
