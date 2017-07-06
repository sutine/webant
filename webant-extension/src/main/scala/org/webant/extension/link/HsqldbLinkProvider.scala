package org.webant.extension.link

import java.sql.SQLException

import org.apache.commons.dbutils.handlers.BeanListHandler
import org.apache.log4j.LogManager
import org.webant.commons.entity.Link
import org.webant.commons.link.JdbcLinkProvider

import scala.collection.JavaConverters._

class HsqldbLinkProvider extends JdbcLinkProvider {
  private val logger = LogManager.getLogger(classOf[HsqldbLinkProvider])

  DRIVER = "org.hsqldb.jdbc.JDBCDriver"

  override def init(params: java.util.Map[String, Object]): Boolean = {
//    url = "jdbc:hsqldb:hsql://localhost;sql.syntax_mys=true"
//    url = "jdbc:hsqldb:file:D:/workspace/webant/data/hsqldb/webant;sql.syntax_mys=true"
//    user = "sa"
//    password = ""

    if (!super.init(params)) {
      logger.error("init HsqldbLinkProvider failed!")
      return false
    }

    logger.info(s"init HsqldbLinkProvider success!")
    createTable()
  }

  override def createTable(): Boolean = {
    val sql = "CREATE TABLE IF NOT EXISTS `LINK` (" +
      "  `id` varchar(64) NOT NULL," +
      "  `taskId` varchar(64) DEFAULT NULL," +
      "  `siteId` varchar(64) DEFAULT NULL," +
      "  `url` varchar(1024) DEFAULT NULL," +
      "  `referer` varchar(1024) DEFAULT NULL," +
      "  `priority` smallint(255) DEFAULT NULL," +
      "  `lastCrawlTime` datetime DEFAULT NULL," +
      "  `status` varchar(32) DEFAULT NULL," +
      "  `dataVersion` int(11) DEFAULT NULL," +
      "  `dataCreateTime` TimeStamp DEFAULT NULL," +
      "  `dataUpdateTime` TimeStamp DEFAULT NULL," +
      "  `dataDeleteTime` TimeStamp DEFAULT NULL," +
      "  PRIMARY KEY (`id`)" +
      ")"

    try {
      runner.update(conn, sql)
    } catch {
      case e: SQLException =>
        e.printStackTrace()
        return false
    }

    true
  }

  override def read(): Iterable[Link] = {
    read(Link.LINK_STATUS_INIT, batch)
  }

  private def read(status: String, size: Int): Iterable[Link] = {
    val sql =
      """SELECT "id", "taskId", "siteId", "url", "referer", "priority", "lastCrawlTime", "status", "dataVersion", "dataCreateTime",
        |"dataUpdateTime", "dataDeleteTime" FROM LINK WHERE "status" = ? ORDER by "dataCreateTime" desc LIMIT ?, ?""".stripMargin

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
    } finally {
      conn.setAutoCommit(true)
    }

    links
  }

  override def upsert(link: Link): Int = {
    // no reflection, simple and fast
    val sql = """insert into link ("id", "taskId", "siteId", "url", "referer", "priority", "lastCrawlTime", "status", "dataVersion", "dataCreateTime",
      |"dataUpdateTime", "dataDeleteTime" ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE
      |"taskId" = ?, "siteId" = ?, "url" = ?, "referer" = ?, "priority" = ?, "lastCrawlTime" = ?, "status" = ?, "dataVersion" = "dataVersion" + 1, "dataUpdateTime" = now()""".stripMargin

    val values = Array[Object](
      link.getId, link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime,
      link.getStatus, link.getDataVersion, link.getDataCreateTime, link.getDataUpdateTime, link.getDataDeleteTime,

      link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime, link.getStatus
    )
    runner.update(conn, sql, values: _*)
  }

  override def upsert(links: Iterable[Link]): Int = {
    // no reflection, simple and fast
    val placeholders = links.map(_ => "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )").mkString(", ")
    val sql = s"""insert into link ("id", "taskId", "siteId", "url", "referer", "priority", "lastCrawlTime", "status", "dataVersion", "dataCreateTime",
                |"dataUpdateTime", "dataDeleteTime" ) values $placeholders ON DUPLICATE KEY UPDATE
                |"dataVersion" = "dataVersion" + 1, "dataUpdateTime" = now()""".stripMargin

    val values = links.flatMap(link => Array(link.getId, link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime,
      link.getStatus, link.getDataVersion, link.getDataCreateTime, link.getDataUpdateTime, link.getDataDeleteTime)).toArray
    runner.update(conn, sql, values: _*)
  }

  private def update(links: Iterable[Link]): Int = {
    // no reflection, simple and fast
    val placeholders = links.map(_ => "( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )").mkString(", ")
    val sql = s"""insert into link ("id", "taskId", "siteId", "url", "referer", "priority", "lastCrawlTime", "status", "dataVersion", "dataCreateTime",
                  |"dataUpdateTime", "dataDeleteTime" ) values $placeholders ON DUPLICATE KEY UPDATE
                  |"priority" = values("priority"), "lastCrawlTime" = values("lastCrawlTime"), "status" = values("status"),
                  |"dataVersion" = "dataVersion" + 1, "dataUpdateTime" = now()""".stripMargin

    val values = links.flatMap(link => Array(link.getId, link.getTaskId, link.getSiteId, link.getUrl, link.getReferer, link.getPriority, link.getLastCrawlTime,
      link.getStatus, link.getDataVersion, link.getDataCreateTime, link.getDataUpdateTime, link.getDataDeleteTime)).toArray
    runner.update(conn, sql, values: _*)
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
}
