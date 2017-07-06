package org.webant.plugin.test.gushiwen

import java.io.IOException
import java.nio.charset.Charset

import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit
import org.webant.plugin.zhihu.processor.{AnswersProcessor, ZhihuSeedProcessor}

class GuShiWenCrawlerTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
  }

  @Test
  def testLoadIds(): Unit = {
    val seed = new ZhihuSeedProcessor
    val answer = new AnswersProcessor
    val ids = seed.links()
    val remainIds = ids.filter(answer.accept)
    println(remainIds.size)
  }

  @Test
  def testRegex(): Unit = {
    val regex = "https://www.zhihu.com/api/v4/members/[0-9a-zA-Z-]*/answers?[\\w\\W]*"
    val url = "https://www.zhihu.com/api/v4/members/ma-en-32/answers?include=data%5B*%5D.is_normal%2Cis_collapsed%2Ccollapse_reason%2Csuggest_edit%2Ccomment_count%2Ccan_comment%2Ccontent%2Cvoteup_count%2Creshipment_settings%2Ccomment_permission%2Cmark_infos%2Ccreated_time%2Cupdated_time%2Creview_info%2Crelationship.is_authorized%2Cvoting%2Cis_author%2Cis_thanked%2Cis_nothelp%2Cupvoted_followees%3Bdata%5B*%5D.author.badge%5B%3F(type%3Dbest_answerer)%5D.topics&offset=20&limit=20&sort_by=created"

    println(url.matches(regex))
  }

  @Test
  @throws[IOException]
  def crawl() {
    val url = "http://www.gushiwen.org/default_3.aspx"
    val body = ""
    val resp = org.apache.http.client.fluent.Request.Get(url)
//      .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
      .addHeader("Cache-Control", "max-age=0")
      .addHeader("Connection", "keep-alive")
      .addHeader("Cookie", "ASP.NET_SessionId=53xs3mimhjnhuoprpehzws0i; Hm_lvt_04660099568f561a75456483228a9516=1498717319; Hm_lpvt_04660099568f561a75456483228a9516=1498720515")
//      .addHeader("If-Modified-Since", "Wed, 28 Jun 2017 08:59:13 GMT")
      .addHeader("Referer", "http://www.gushiwen.org/")
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    println(result)
  }
}
