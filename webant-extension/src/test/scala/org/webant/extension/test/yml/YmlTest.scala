package org.webant.extension.test.yml

import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.yaml.snakeyaml.Yaml

import scala.beans.BeanProperty

/**
  * Created by Administrator on 2017/7/8.
  */
class YmlTest extends AssertionsForJUnit {

  @Test
  def testYml(): Unit = {
    val source =
      """
        |--- !!org.webant.extension.test.yml.EmailConfigYaml
        |host : smtp.sina.cn
        |username : cjuexuan
        |password : password
        |auth : true
        |fromEmail: cjuexuan@sina.cn
        |
      """.stripMargin
    val yml = new Yaml()
    val email = yml.load(source).asInstanceOf[EmailConfigYaml]
    println(s"yaml2email:$email")
    val emailString = yml.dump(email)
    println(s"email2yaml:$emailString")
  }
}

class EmailConfigYaml {
  @BeanProperty var host: String = _
  @BeanProperty var username: String = _
  @BeanProperty var password: String = _
  @BeanProperty var auth: Boolean = _
  @BeanProperty var fromEmail: String = _
  override def toString = s"EmailConfig($host,$username,$password,$auth,$fromEmail)"
}