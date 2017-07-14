package org.webant.worker.link

import java.util

import org.apache.log4j.LogManager
import org.webant.commons.entity.Link
import org.webant.commons.link.JdbcLinkProvider

class HttpLinkProvider extends JdbcLinkProvider {
  private val logger = LogManager.getLogger(classOf[HttpLinkProvider])

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
    true
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
}
