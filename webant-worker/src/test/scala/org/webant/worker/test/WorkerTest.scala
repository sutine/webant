package org.webant.worker.test

import java.io.File
import java.lang.reflect.{Field, InvocationTargetException}
import java.text.ParseException
import java.util.Date

import com.google.gson.GsonBuilder
import org.apache.commons.beanutils.{BeanUtils, PropertyUtils}
import org.apache.commons.io.FileUtils
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit
import org.webant.worker.config.SiteConfig

import scala.beans.BeanProperty

class WorkerTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
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
  def testBeanUtils {
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
    var name: String = null
    @BeanProperty
    var age: Int = 0
    @BeanProperty
    var birthday: Date = null
  }

}
