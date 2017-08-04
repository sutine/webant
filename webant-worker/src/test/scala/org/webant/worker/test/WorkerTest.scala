package org.webant.worker.test

import java.io.File
import java.lang.reflect.{Field, InvocationTargetException}
import java.text.ParseException
import java.util.Date
import javax.xml.bind.{JAXBContext, JAXBException}

import com.google.gson.GsonBuilder
import org.apache.commons.beanutils.{BeanUtils, PropertyUtils}
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit
import org.webant.commons.entity.SiteConfig
import org.webant.worker.config.{SiteConfigBuilder, WorkerConfig}

import scala.beans.BeanProperty

class WorkerTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
  }

  @Test
  def printSql(): Unit = {
    val table = "link"
    val sql = s"CREATE TABLE IF NOT EXISTS `$table` (" +
      "  `id` varchar(64) NOT NULL," +
      "  `taskId` varchar(64) DEFAULT NULL," +
      "  `siteId` varchar(64) DEFAULT NULL," +
      "  `nodeId` varchar(64) DEFAULT NULL," +
      "  `url` varchar(1024) DEFAULT NULL," +
      "  `body` text DEFAULT NULL," +
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

    println(sql)
  }

  @Test
  def testWorkerConfig(): Unit = {
    val path = "worker.xml"
    WorkerConfig(path)
  }

  @Test
  def testConfigToXml(): Unit = {
    val configPath = ClassLoader.getSystemResource("site/mahua.json").getPath
    val config = new SiteConfigBuilder().loadSiteConfig(configPath).build()
    try {
      val context = JAXBContext.newInstance(classOf[SiteConfig])
      val marshaller = context.createMarshaller
      marshaller.marshal(config, System.out)
    } catch {
      case e: JAXBException =>
        e.printStackTrace()
    }

//    println(config.toString)

  }

  @Test
  def testXml(): Unit = {
    val path = "worker.xml"
    val configPath = ClassLoader.getSystemResource(path)
    require(StringUtils.isNotBlank(path), "worker config path can not be empty.")
    val file = new File(configPath.getPath)
    require(file.exists(), "worker config does not exists.")
    require(file.isFile, "worker config can not be a directory.")

//    println(xml.toString())
  }

  @Test
  def testEncode(): Unit = {
    val content = "\\u76f4\\u64ad\\u8fbe\\u4eba"
    val newStr = new String(content.getBytes("iso8859-1"), "UTF-8")
    println(newStr)
  }

  @Test
  def testRegex(): Unit = {
    val regex = "http://www.toutiao.com/\\w*\\d*/"
    val url = "http://www.toutiao.com/6432977727803884034/"

    println(url.matches(regex))
  }

  @Test
  def testFromJson(): Unit = {
    val configPath = ClassLoader.getSystemResource("mahua_site.json")
    if (configPath == null) return

    val config = FileUtils.readFileToString(new File(configPath.getPath), "UTF-8")

    val builder = new GsonBuilder
//    builder.registerTypeAdapter(classOf[BaseWorkerProcessor], new NewsProviderInstanceCreator(0))
    val gson = builder.create()
    val siteConfig = gson.fromJson(config, classOf[SiteConfig])
    println(siteConfig.id)
  }

  @Test
  @throws[InstantiationException]
  @throws[IllegalAccessException]
  @throws[ParseException]
  @throws[InvocationTargetException]
  @throws[NoSuchMethodException]
  def testBeanUtils() {
    val person: Person = new Person
    person.name = "name1"
    person.age = 20
    person.birthday = new Date
    val fields: Array[Field] = person.getClass.getDeclaredFields

    fields.foreach(field => {
      val name: String = field.getName
      if (PropertyUtils.isReadable(person, name) && PropertyUtils.isWriteable(person, name)) {
        System.out.println(name + " : " + BeanUtils.getProperty(person, name))
      }
    })
  }

  class Person {
    @BeanProperty
    var name: String = _
    @BeanProperty
    var age: Int = 0
    @BeanProperty
    var birthday: Date = _
  }

}
