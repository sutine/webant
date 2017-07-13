package org.webant.extension.test.sqlite

import java.sql.DriverManager

import junit.framework.TestCase
import org.junit.Test
import org.webant.commons.entity.Link
import org.webant.commons.link.Progress
import org.webant.commons.utils.JsonUtils
import org.webant.extension.link.SqlitelLinkProvider

class SqliteTest extends TestCase {

  @Test
  def testProgressSum(): Unit = {
    val p1 = Progress(1, 2, 3, 4, 5)
    val p2 = Progress(10, 20, 30, 40, 50)
    val p3 = Progress(100, 200, 300, 400, 500)
    val r = Iterable(p1, p2, p3)
    val s = r.reduce((left, right) => {
      Progress(left.total + right.total, left.init + right.init, left.pending + right.pending, left.success + right.success, left.fail + right.fail)
    })
    println(s)
  }

  @Test
  def createDbFile(): Unit = {
    // 连接SQLite的JDBC
    Class.forName("org.sqlite.JDBC")

    // 建立一个数据库名test.db的连接，如果不存在就在当前目录下创建之
    val start = System.currentTimeMillis()
    //    val conn = DriverManager.getConnection("jdbc:sqlite::resource:webant.db")
    val conn = DriverManager.getConnection("jdbc:sqlite:data/webant.db")
    val end = System.currentTimeMillis()
    System.out.println("创建数据库文件并连接耗费时间：" + (end - start))

    conn.close()
  }

  @Test
  def createDatabase(): Unit = {
    val createDatabaseSql = "CREATE DATABASE IF NOT EXISTS WEBANT"
    val createTableSql = "CREATE TABLE IF NOT EXISTS `link` (" +
      "  `id` varchar(64) NOT NULL," +
      "  `taskId` varchar(64) DEFAULT NULL," +
      "  `siteId` varchar(64) DEFAULT NULL," +
      "  `url` varchar(255) DEFAULT NULL," +
      "  `referer` varchar(255) DEFAULT NULL," +
      "  `priority` smallint(255) DEFAULT NULL," +
      "  `lastCrawlTime` datetime DEFAULT NULL," +
      "  `status` varchar(32) DEFAULT NULL," +
      "  `dataVersion` varchar(255) DEFAULT NULL," +
      "  `dataCreateTime` datetime DEFAULT NULL," +
      "  `dataUpdateTime` datetime DEFAULT NULL," +
      "  `dataDeleteTime` datetime DEFAULT NULL," +
      "  PRIMARY KEY (`id`)" +
      ")"
    Class.forName("org.sqlite.JDBC")
    val conn = DriverManager.getConnection("jdbc:sqlite:data/webant.db")
    val stat = conn.createStatement()

    stat.executeUpdate(createDatabaseSql)
    stat.executeUpdate(createTableSql)

    val selectLink = "SELECT id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, dataCreateTime, dataUpdateTime, dataDeleteTime FROM link WHERE status = ? ORDER by ? ? LIMIT ?, ?"
    val upsert = "insert into link ( id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, dataCreateTime, dataUpdateTime ) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ON DUPLICATE KEY UPDATE taskId = ?, siteId = ?, url = ?, referer = ?, priority = ?, lastCrawlTime = ?, status = ?, dataVersion = dataVersion + 1, dataUpdateTime = now()"
    val upserts = "insert into link (id, taskId, siteId, url, referer, priority, lastCrawlTime, status, dataVersion, dataCreateTime, dataUpdateTime, dataDeleteTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) , (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE dataVersion = dataVersion + 1, dataUpdateTime = now() -- url = values(url), referer = values(referer), priority = values(priority), lastCrawlTime = values(lastCrawlTime), status = values(status), dataVersion = dataVersion + 1, dataUpdateTime = now()"
    conn.close()
  }

  @Test
  def testLinkProvider(): Unit = {
    val provider = new SqlitelLinkProvider
//    provider.init()

    val link1 = new Link("id1", "娃娃1", "test6")
    val link2 = new Link("id2", "娃娃5", "test7")
    val link3 = new Link("id3", "娃娃8", "test9")
    val link4 = new Link("id4", "娃娃8", "test9")
    val links = Iterable(link1, link2, link3, link4)

    val json = JsonUtils.toJson(link1)
    val obj = JsonUtils.fromJson(json, classOf[java.util.HashMap[String, AnyVal]])

    provider.write(link4)
  }

  @Test
  def testLinkUpdate(): Unit = {
    val provider = new SqlitelLinkProvider
//    provider.init()

    val link1 = new Link("id4", "娃娃1", "test6")
    provider.update(link1)
  }

  @Test
  def testLinkQuery(): Unit = {
    val provider = new SqlitelLinkProvider
//    provider.init()

    val list = provider.read()
    list.foreach(link => println(link.getId))
  }
}
