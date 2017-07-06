package org.webant.worker.link

import java.sql.SQLException

import org.apache.commons.dbutils.handlers.BeanListHandler
import org.apache.commons.lang3.reflect.FieldUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.Link
import org.webant.commons.utils.BeanUtils

import scala.collection.JavaConverters._

class DerbyLinkProvider extends JdbcLinkProvider {
  private val logger = LogManager.getLogger(classOf[DerbyLinkProvider])

  DRIVER = "org.apache.derby.jdbc.EmbeddedDriver"

  override def init(params: java.util.Map[String, Object]): Boolean = {
//    val url = "jdbc:derby:data/derby/data;create=true"
//    val user = ""
//    val password = ""

    if (!super.init(params)) {
      logger.error("init DerbyLinkProvider failed!")
      return false
    }

    logger.info(s"init DerbyLinkProvider success!")
    createTable()
  }

  override def createTable(): Boolean = {
    val sql = "CREATE TABLE link (" +
      "  id varchar(64) NOT NULL," +
      "  taskId varchar(64) DEFAULT NULL," +
      "  siteId varchar(64) DEFAULT NULL," +
      "  url varchar(1024) DEFAULT NULL," +
      "  referer varchar(1024) DEFAULT NULL," +
      "  priority smallint DEFAULT NULL," +
      "  lastCrawlTime TimeStamp DEFAULT NULL," +
      "  status varchar(32) DEFAULT NULL," +
      "  dataVersion int DEFAULT NULL," +
      "  dataCreateTime TimeStamp DEFAULT NULL," +
      "  dataUpdateTime TimeStamp DEFAULT NULL," +
      "  dataDeleteTime TimeStamp DEFAULT NULL," +
      "  PRIMARY KEY (id)" +
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
    val links = read(Link.LINK_STATUS_INIT, batch)
    if (links.nonEmpty) {
      val pending = links.map(link => {
        link.setStatus(Link.LINK_STATUS_PENDING)
        link
      })

      update(pending)
    }

    links
  }

  private def read(status: String, size: Int): Iterable[Link] = {
    val sql = "SELECT * " +
      s"FROM link WHERE status = ? ORDER by dataCreateTime desc OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"

    val offset: Integer = 0
    val pageSize: Integer = if (size <= 0 || size > 1000) 1000 else size
    val selectParams = Array[Object](status, offset, pageSize)

    var rs = Iterable.empty[Link]

    conn.setAutoCommit(false)
    try {
      rs = runner.query(conn, sql, new BeanListHandler[Link](classOf[Link]), selectParams: _*).asScala
      if (rs.nonEmpty) {
        val updateSql = "update link set status = ? where id = ?"
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

  def update(links: Iterable[Link]): Int = {
    0
  }

  override def write(link: Link): Int = {
    require(conn != null)
    if (link == null) return 0
    insert(link)
  }

  def insert(link: Link): Int = {
    require(conn != null)
    if (link == null) return 0
    val fieldNames = BeanUtils.getDeclaredFields(link).map(_.getName)
    val columns = fieldNames.mkString("(", ", ", ")")
    val placeholders = fieldNames.map(_ => "?").mkString("(", ",", ")")
    val values = fieldNames.map(fieldName => FieldUtils.readField(link, fieldName, true))

    val sql = s"insert into link $columns values $placeholders"
    runner.update(conn, sql, values: _*)
  }

  override def write(links: Iterable[Link]): Int = {
    require(conn != null)
    if (links == null || links.isEmpty) return 0

    val fieldNames = BeanUtils.getDeclaredFields(links.head).map(_.getName)
    val columns = fieldNames.mkString("(", ", ", ")")
    val placeholders = fieldNames.map(_ => "?").mkString("(", ",", ")")
    val sql = s"insert into link $columns values $placeholders"

    val params = links.map(link => {
      fieldNames.map(fieldName => FieldUtils.readField(link, fieldName, true))
    }).toArray

    runner.batch(conn, sql, params).sum
  }

  override def update(link: Link): Int = {
    require(conn != null)
    if (link == null) return 0
    val fieldNames = BeanUtils.getDeclaredFields(link).map(_.getName)
    val excludes = Set("id")
    val filterFieldNames = fieldNames.filter(!excludes.contains(_))
    val filterPlaceholders = filterFieldNames.map(fieldName => s"$fieldName = ?").mkString(", ")
    val filterValues = filterFieldNames.map(fieldName => FieldUtils.readField(link, fieldName, true))

    val sql = s"update link set $filterPlaceholders, dataVersion = dataVersion + 1, dataUpdateTime = datetime('now', 'localtime') where id = '${link.getId}'"
    //    val sql = s"insert into fun $columns values $placeholders ON DUPLICATE KEY UPDATE dataVersion = dataVersion + 1, dataUpdateTime = now()"
    runner.update(conn, sql, filterValues: _*)
  }

  override def upsert(link: Link): Int = {
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

    val sql = s"insert into link $columns values $placeholders ON DUPLICATE KEY UPDATE $filterPlaceholders, dataVersion = dataVersion + 1, dataUpdateTime = now()"
    println(sql)
    runner.update(conn, sql, allValues: _*)
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
    }

    true
  }
}
