package org.webant.plugin.test.qiushibaike

import java.io.IOException
import java.nio.charset.Charset

import org.jsoup.Jsoup
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit

class QiushibaikeCrawlerTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
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
    val url = "https://www.qiushibaike.com/article/118790265"
    val body = "web_csrf_token=undefined&mode=1&typelogin=1%2F&piccode=7ug2&username=snsant&password=1qa%40WS3ed"
    val referer = "http://chuanbo.weiboyi.com/"

    val resp = org.apache.http.client.fluent.Request.Get(url)
      //      .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED)
      //      .addHeader("Proxy-Connection", "keep-alive")
      //      .addHeader("Pragma", "no-cache")
      //      .addHeader("Cache-Control", "no-cache")
      //      .addHeader("Accept", "*/*")
      //      .addHeader("Accept-Encoding", "gzip, deflate")
      //      .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
      //      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
      //      .addHeader("X-Requested-With", "XMLHttpRequest")
      //      .addHeader("Cookie", "PHPSESSID=er4h7nvttver6s02saamnsosu3; TRACK_DETECTED=1.0.1; TRACK_BROWSER_ID=4a734947754705a34ad87b86f200ed5b; Hm_lvt_29d7c655e7d1db886d67d7b9b3846aca=1498049922; Hm_lpvt_29d7c655e7d1db886d67d7b9b3846aca=1498898314; Hm_lvt_9a2792b12b6388cfcc41e508c781a8be=1498049923; Hm_lpvt_9a2792b12b6388cfcc41e508c781a8be=1498898315; aLastLoginTime=1498898312; loginHistoryRecorded=0; TRACK_USER_ID=422902; TRACK_IDENTIFY_AT=2017-07-01T08%3A39%3A14.856Z; TRACK_SESSION_ID=f7c48941cd7630221917d85997c07284; Hm_lvt_5ff3a7941ce54a1ba102742f48f181ab=1498098069,1498202058,1498898237,1498898356; Hm_lpvt_5ff3a7941ce54a1ba102742f48f181ab=1498898356; _gscu_867320846=9804992204kk0467; _gscs_867320846=t98898213bi5bcy16|pv:5; _gscbrs_867320846=1; username=; rememberusername=")
      //      .addHeader("DNT", "1")
      //      .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
      //      .addHeader("Referer", referer)
      //      .addHeader("Origin", "http://chuanbo.weiboyi.com")
      //      .addHeader("Host", "chuanbo.weiboyi.com")
      //      .addHeader("Proxy-Connection", "keep-alive")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    val doc = Jsoup.parse(result)
    val profileUrl = doc.select(".newArticleHead a").attr("href")
    val avatarUrl = doc.select(".newArticleHead img").attr("src")
    val userName = doc.select(".newArticleHead .touch-user-name-a").text()
    val title = doc.select(".content-text").text()
    val imgUrl = doc.select(".content-text img").attr("src")
    val likeNum = doc.select(".article_info .laugh-comment").attr("data-votes")
    val commentNum = doc.select(".article_info .comments").text().split(" ")(0)
    System.out.println(title)
  }
}
