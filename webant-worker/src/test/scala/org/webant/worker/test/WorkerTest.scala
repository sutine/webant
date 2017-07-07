package org.webant.worker.test

import java.io.File
import java.lang.reflect.{Field, InvocationTargetException}
import java.text.ParseException
import java.util.Date

import com.google.gson.GsonBuilder
import org.apache.commons.beanutils.{BeanUtils, PropertyUtils}
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit
import org.webant.worker.config.{SiteConfig, WorkerConfig}

import scala.beans.BeanProperty
import scala.xml._

class WorkerTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
  }

  @Test
  def testWorkerConfig(): Unit = {
    val path = "worker.xml"
    WorkerConfig(path)
  }

  @Test
  def testXml(): Unit = {
    val path = "worker.xml"
    val configPath = ClassLoader.getSystemResource(path)
    require(StringUtils.isNotBlank(path), "worker config path can not be empty.")
    val file = new File(configPath.getPath)
    require(file.exists(), "worker config does not exists.")
    require(file.isFile, "worker config can not be a directory.")
    val xml = XML.loadFile(file)
    val id = xml\\"worker"\\"id"text
    val name = xml\\"name"\\"id"text

    println(xml\\"worker"text)

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
