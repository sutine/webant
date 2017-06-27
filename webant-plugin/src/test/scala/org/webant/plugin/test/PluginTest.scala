package org.webant.plugin.test

import java.nio.charset.Charset

import org.apache.http.client.fluent.Response
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit

class PluginTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
  }

  @Test
  def testRegex(): Unit = {
    val regex = "http://user.mahua.com/ajax/joke/checkJokesDynamic[\\w\\W]*"
    val url = "http://user.mahua.com/ajax/joke/checkJokesDynamic?callback=jQuery17209865076656443552_1498566208473&joke_ids=1679937%2C1679936%2C1679935%2C1679934%2C1679933%2C1679932%2C1679928%2C1679927%2C1679929%2C1679931%2C1679930%2C1679926%2C1679923%2C1679921%2C1679919%2C1679918%2C1679925%2C1679924%2C1679922%2C1679920&_=1498566208688"

    println(url.matches(regex))
  }

  @Test
  def testGetVoteUpDown(): Unit = {
    val url = "http://user.mahua.com/ajax/joke/checkJokesDynamic?callback=jQuery&joke_ids=1679937&_=1498571259447"

    val resp: Response = org.apache.http.client.fluent.Request.Get(url)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/json;q=0.9,image/webp,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate, sdch")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("Proxy-Connection", "keep-alive")
      .addHeader("DNT", "1")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    println(result)
  }
}
