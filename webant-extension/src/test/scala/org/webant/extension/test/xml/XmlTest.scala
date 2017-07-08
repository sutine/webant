package org.webant.extension.test.xml

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

class XmlTest extends AssertionsForJUnit {

  @Test
  def XMLTobean(): Unit = {
    import javax.xml.bind.{JAXBContext, JAXBException}
    val p = new Person(1, "小红", "10", "北京")
    val ob = new ObjectDemo("小明", "11", p)

    try {
      val context = JAXBContext.newInstance(classOf[ObjectDemo])
      val marshaller = context.createMarshaller
      marshaller.marshal(ob, System.out)
    } catch {
      case e: JAXBException =>
        e.printStackTrace()
    }

    println(ob.toString)
  }

  @Test
  def beanToXML(): Unit = {
    import java.io.StringReader
    import javax.xml.bind.{JAXBContext, JAXBException}
    val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<objectDemo>" + "<name>小明</name>" + "<person>" + "<address>北京</address>" + "<age>10</age>" + "<id>1</id>" + "<username>小红</username>" + "</person>" + "<sex>11</sex>" + "</objectDemo>"
    try {
      val context = JAXBContext.newInstance(classOf[ObjectDemo])
      val unmarshaller = context.createUnmarshaller
      val obj = unmarshaller.unmarshal(new StringReader(xml)).asInstanceOf[ObjectDemo]
      System.out.println(obj.getName)
      System.out.println(obj.getPerson.getAddress)
    } catch {
      case e: JAXBException =>
        e.printStackTrace()
    }
  }
}


import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement class ObjectDemo() //无参构造方法
{
  private var name:String = _
  private var sex:String = _
  private var person:Person = _

  /**
    * @return the person
    */
  def getPerson: Person = person

  /**
    * @param person the person to set
    */
  def setPerson(person: Person): Unit = {
    this.person = person
  }

  /**
    * @return the name
    */
  def getName: String = name

  /**
    * @param name the name to set
    */
  def setName(name: String): Unit = {
    this.name = name
  }

  /**
    * @return the sex
    */
  def getSex: String = sex

  /**
    * @param sex the sex to set
    */
  def setSex(sex: String): Unit = {
    this.sex = sex
  }

  //有参构造方法
  def this(name: String, sex: String, person: Person) {
    this()
    this.name = name
    this.sex = sex
    this.person = person
  }
}

class Person() //无参构造函数
{
  private var id = 0
  private var username: String = _
  private var age: String = _
  private var address: String = _

  def getId: Int = id

  def setId(id: Int): Unit = {
    this.id = id
  }

  def getUsername: String = username

  def setUsername(username: String): Unit = {
    this.username = username
  }

  def getAge: String = age

  def setAge(age: String): Unit = {
    this.age = age
  }

  def getAddress: String = address

  def setAddress(address: String): Unit = {
    this.address = address
  }

  //有参构造函数
  def this(id: Int, username: String, age: String, address: String) {
    this()
    this.id = id
    this.username = username
    this.age = age
    this.address = address
  }
}