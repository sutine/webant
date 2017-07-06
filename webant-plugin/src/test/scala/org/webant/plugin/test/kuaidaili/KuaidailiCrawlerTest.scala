package org.webant.plugin.test.kuaidaili

import java.io.IOException
import java.nio.charset.Charset

import org.jsoup.Jsoup
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit

import scala.collection.JavaConverters._

class KuaidailiCrawlerTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
  }

  @Test
  @throws[IOException]
  def crawl() {
    val regex = "http://www.kuaidaili.com/free/?\\w*/?\\d*/?"
    val url = "http://www.kuaidaili.com/free/intr/1714/"
    if (!url.matches(regex))
      return

    val resp = org.apache.http.client.fluent.Request.Get(url)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
      .addHeader("Cache-Control", "max-age=0")
      .addHeader("Connection", "keep-alive")
      .addHeader("Cookie", "channelid=0; sid=1499250960617393; _ga=GA1.2.1752332708.1497859110; _gid=GA1.2.235526105.1499251241; Hm_lvt_7ed65b1cc4b810e9fd37959c9bb51b31=1497859110; Hm_lpvt_7ed65b1cc4b810e9fd37959c9bb51b31=1499252564")
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    val doc = Jsoup.parse(result, url)

    val tds = doc.select("#list tbody tr").asScala.map(tr => {
      tr.select("td").asScala.map(td => {
        td.text()
      })
    })
    println(tds)
  }
}
