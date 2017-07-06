package org.webant.commons.link

import java.sql.{Connection, DriverManager, SQLException}

import org.apache.commons.collections.MapUtils
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.dbutils.handlers.{ArrayListHandler, ScalarHandler}
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.reflect.FieldUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.Link
import org.webant.commons.utils.BeanUtils

import scala.collection.JavaConverters._

trait JdbcLinkProvider extends ILinkProvider {
  private val logger = LogManager.getLogger(classOf[JdbcLinkProvider])
  private val threadLocal = new ThreadLocal[Connection]()
  protected val runner: QueryRunner = new QueryRunner()
  protected var DRIVER: String = _
  protected var taskId: String = _
  protected var siteId: String = _
  protected var table: String = _
  protected var conn: Connection = _

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("url") || !params.containsKey("username")
      || !params.containsKey("password") || !params.containsKey("siteId"))
      return false

    val url = MapUtils.getString(params, "url")
    val username = MapUtils.getString(params, "username")
    val password = MapUtils.getString(params, "password")
    batch = MapUtils.getInteger(params, "batch", 20)
    taskId = MapUtils.getString(params, "taskId")
    siteId = MapUtils.getString(params, "siteId")
    table = s"${taskId}_${siteId}_link"

    conn = getConnection(url, username, password)

    conn != null
  }

  protected def createTable(): Boolean = {
    val sql = s"CREATE TABLE IF NOT EXISTS `$table` (" +
      "  `id` varchar(64) NOT NULL," +
      "  `taskId` varchar(64) DEFAULT NULL," +
      "  `siteId` varchar(64) DEFAULT NULL," +
      "  `url` varchar(1024) DEFAULT NULL," +
      "  `referer` varchar(1024) DEFAULT NULL," +
      "  `priority` smallint(255) DEFAULT NULL," +
      "  `lastCrawlTime` datetime DEFAULT NULL," +
      "  `status` varchar(32) DEFAULT NULL," +
      "  `dataVersion` int(11) DEFAULT NULL," +
      "  `dataCreateTime` datetime DEFAULT NULL," +
      "  `dataUpdateTime` datetime DEFAULT NULL," +
      "  `dataDeleteTime` datetime DEFAULT NULL," +
      "  PRIMARY KEY (`id`)," +
      s"  KEY `idx_${table}_taskId` (`taskId`)," +
      s"  KEY `idx_${table}_siteId` (`siteId`)," +
      s"  KEY `idx_${table}_priority` (`priority`)," +
      s"  KEY `idx_${table}_status` (`status`)," +
      s"  KEY `idx_${table}_dataCreateTime` (`dataCreateTime`)," +
      s"  KEY `idx_${table}_dataUpdateTime` (`dataUpdateTime`)" +
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

  protected def update(link: Link): Int = {
    require(conn != null)
    if (link == null) return 0
    val fieldNames = BeanUtils.getDeclaredFields(link).map(_.getName)
    val excludes = Set("id")
    val filterFieldNames = fieldNames.filter(!excludes.contains(_))
    val filterPlaceholders = filterFieldNames.map(fieldName => s"$fieldName = ?").mkString(", ")
    val filterValues = filterFieldNames.map(fieldName => FieldUtils.readField(link, fieldName, true))

    val sql = s"update $table set $filterPlaceholders, dataVersion = dataVersion + 1, dataUpdateTime = datetime('now', 'localtime') where id = '${link.getId}'"
    runner.update(conn, sql, filterValues: _*)
  }

  protected def upsert(link: Link): Int = {
    require(conn != null)
    if (link == null) return 0
    val fieldNames = BeanUtils.getDeclaredFields(link).map(_.getName)
    val columns = fieldNames.mkString("(", ", ", ")")
    val placeholders = fieldNames.map(_ => "?").mkString("(", ",", ")")
    val values = fieldNames.map(fieldName => FieldUtils.readField(link, fieldName, true))
    val excludes = Set("id", "dataVersion", "dataCreateTime", "dataUpdateTime", "dataDeleteTime")
    val filterFieldNames = fieldNames.filter(!excludes.contains(_))
    val filterPlaceholders = filterFieldNames.map(fieldName => s"$fieldName = ?").mkString(", ")
    val filterValues = filterFieldNames.map(fieldName => FieldUtils.readField(link, fieldName, true))
    val allValues = (values ++ filterValues).toSeq

    val sql = s"insert into $table $columns values $placeholders ON DUPLICATE KEY UPDATE $filterPlaceholders, dataVersion = dataVersion + 1, dataUpdateTime = now()"
    runner.update(conn, sql, allValues: _*)
  }

  protected def upsert(links: Iterable[Link]): Int = {
    require(conn != null)
    if (links == null || links.isEmpty) return 0

    val fieldNames = BeanUtils.getDeclaredFields(links.head).map(_.getName)
    val columns = fieldNames.mkString("(", ",", ")")
    val placeholders = links.map(_ => fieldNames.map(_ => "?").mkString("(", ",", ")")).mkString(", ")
    val values = links.flatMap(link => fieldNames.map(fieldName => FieldUtils.readField(link, fieldName, true))).toArray
    //    val pairs = fieldNames.map(fieldName => s"$fieldName = values($fieldName)").mkString(", ")

    // if already exists, do nothing
    val sql = s"insert into $table $columns values $placeholders " +
      s"ON DUPLICATE KEY UPDATE dataVersion = dataVersion + 1, dataUpdateTime = now()"
    //      s"ON DUPLICATE KEY UPDATE $pairs, dataVersion = dataVersion + 1, dataUpdateTime = now()"

    runner.update(conn, sql, values: _*)
  }

  // reset pending status to init status to recrawl, fix some abnormal status
  override def resetToInit(status: String): Int = {
    val sql = s"UPDATE $table SET status = 'init' WHERE status = '$status'"
    runner.update(conn, sql)
  }

  override def progress(): Progress = {
    val t = total()

    val sql = s"SELECT status, count(1) FROM $table group by status"
    val result = runner.query(conn, sql, new ArrayListHandler())

    val counts = result.asScala.map(item => (item(0).asInstanceOf[String], item(1).asInstanceOf[Long])).toMap
    val init = if (counts.contains(Link.LINK_STATUS_INIT)) counts(Link.LINK_STATUS_INIT) else 0
    val pending = if (counts.contains(Link.LINK_STATUS_PENDING)) counts(Link.LINK_STATUS_PENDING) else 0
    val success = if (counts.contains(Link.LINK_STATUS_SUCCESS)) counts(Link.LINK_STATUS_SUCCESS) else 0
    val fail = if (counts.contains(Link.LINK_STATUS_FAIL)) counts(Link.LINK_STATUS_FAIL) else 0

    Progress(t, init, pending, success, fail)
  }

  override def total(): Long = {
    val sql = s"SELECT count(1) FROM $table"
    runner.query(conn, sql, new ScalarHandler())
  }

  override def count(status: String): Long = {
    val sql = s"SELECT count(1) FROM $table WHERE status = ?"
    runner.query(conn, sql, new ScalarHandler(), status)
  }

  override def close(): Boolean = {
    if (null != conn) {
      try
        conn.close()
      catch {
        case e: SQLException =>
          e.printStackTrace()
      }
      conn = null
      logger.info(s"close ${getClass.getSimpleName} connection success.")
    }

    true
  }

  protected def getConnection(url: String, user: String, password: String): Connection = {
    require(StringUtils.isNotBlank(url))
    require(StringUtils.isNotBlank(DRIVER))

    var conn: Connection = null
    try {
      Class.forName(DRIVER)
      conn = DriverManager.getConnection(url, user, password)
    }
    catch {
      case e: Exception =>
        e.printStackTrace()
    }
    conn
  }

  protected def getConnectionThreadLocal(url: String, user: String, password: String): Connection = {
    require(StringUtils.isNotBlank(url))
    require(StringUtils.isNotBlank(DRIVER))

    if (threadLocal.get() == null) {
      conn = DriverManager.getConnection(url)
      threadLocal.set(conn)
      conn
    } else {
      threadLocal.get()
    }
  }

}
