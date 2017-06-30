package org.webant.worker.store

import java.sql.{Connection, DriverManager, SQLException}

import org.apache.commons.collections.MapUtils
import org.apache.commons.dbutils.QueryRunner
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.reflect.FieldUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.HttpDataEntity
import org.webant.commons.utils.BeanUtils

trait JdbcStoreProvider[T <: HttpDataEntity] extends IStore[T] {
  private val logger = LogManager.getLogger(classOf[JdbcStoreProvider[T]])
  private val threadLocal = new ThreadLocal[Connection]()
  protected val runner: QueryRunner = new QueryRunner()
  protected var DRIVER: String = _
  protected var conn: Connection = _

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("url") || !params.containsKey("username") || !params.containsKey("password"))
      return false

    val url = MapUtils.getString(params, "url")
    val username = MapUtils.getString(params, "username")
    val password = MapUtils.getString(params, "password")

    conn = getConnection(url, username, password)

    conn != null
  }

  protected def createTable(): Boolean = {
    val sql = "CREATE TABLE IF NOT EXISTS data (" +
      "  id varchar(64)," +
      "  data text DEFAULT NULL," +
      "  srcId varchar(64) DEFAULT NULL," +
      "  source varchar(64) DEFAULT NULL," +
      "  srcUrl varchar(1024) DEFAULT NULL," +
      "  crawlTime datetime DEFAULT NULL," +
      "  dataVersion int(11) DEFAULT NULL," +
      "  dataCreateTime datetime DEFAULT NULL," +
      "  dataUpdateTime datetime DEFAULT NULL," +
      "  dataDeleteTime datetime DEFAULT NULL," +
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

  override def upsert(data: T): Int = {
    require(conn != null)
    if (data == null) return 0
    val fieldNames = BeanUtils.getDeclaredFields(data).map(_.getName)
    val columns = fieldNames.mkString("(", ",", ")")
    val placeholders = fieldNames.map(_ => "?").mkString("(", ",", ")")
    val sql = s"insert into fun $columns values $placeholders ON DUPLICATE KEY UPDATE dataVersion = dataVersion + 1, dataUpdateTime = now()"
    val values = fieldNames.map(fieldName => FieldUtils.readField(data, fieldName, true))

    var affectRowCount = 0

    try {
      affectRowCount = runner.update(conn, sql, values: _*)
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
    }

    affectRowCount
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
      logger.info("close LinkProvider connection success.")
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
