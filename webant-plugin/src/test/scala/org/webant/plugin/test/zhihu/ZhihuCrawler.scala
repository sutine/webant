package org.webant.plugin.test.zhihu

import java.io.IOException
import java.nio.charset.Charset

import org.apache.http.client.fluent.Response
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit
import org.webant.plugin.zhihu.processor.{AnswersProcessor, ZhihuSeedProcessor}

class ZhihuCrawler extends AssertionsForJUnit {

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
    val url: String = "https://www.zhihu.com/api/v4/members/huang-jie-rui-23/answers?offset=0&limit=20&sort_by=created&include=data[*].is_normal,is_collapsed,collapse_reason,suggest_edit,comment_count,can_comment,content,voteup_count,reshipment_settings,comment_permission,mark_infos,created_time,updated_time,review_info,relationship.is_authorized,voting,is_author,is_thanked,is_nothelp,upvoted_followees;data[*].author.badge[?(type=best_answerer)].topics"
    val body: String = "offset=20&limit=20&sort_by=created&include=data[*].is_normal,is_collapsed,collapse_reason,suggest_edit,comment_count,can_comment,content,voteup_count,reshipment_settings,comment_permission,mark_infos,created_time,updated_time,review_info,relationship.is_authorized,voting,is_author,is_thanked,is_nothelp,upvoted_followees;data[*].author.badge[?(type=best_answerer)].topics"
    val referer: String = "https://www.zhihu.com/people/sgai/answers"
    val resp: Response = org.apache.http.client.fluent.Request.Get(url)
//      .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED)
      .addHeader("Accept", "application/json, text/plain, */*")
      .addHeader("Accept-Encoding", "gzip, deflate, br")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
      .addHeader("authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20")
      .addHeader("Connection", "keep-alive")
      .addHeader("Cookie", "q_c1=4e2c8fb281aa49f7a1a4c8f88297eb25|1497321957000|1497321957000; q_c1=ce4133c29ce34b8cb8a3d2f217706cee|1497321957000|1497321957000; aliyungf_tc=AQAAAOHxiwPxPQUAUb0Qt6/uDktO7KJF; capsion_ticket=\"2|1:0|10:1497930998|14:capsion_ticket|44:ZjRkNDVkNDEwYmY1NDZiNjk0ZjhlNjA1NGM3Mjk1ODk=|1f544f44ef3522b9bd1dede68727c906774eef7d8e1efc05e62b642bea3278ac\"; r_cap_id=\"MGQxNmI4MTViNDNkNDdkZDhiNDJjYmMxYjFlZTk2MTk=|1498653197|7a5cc12917ace5c66d6558b8d9639d3bdd9734f9\"; cap_id=\"Y2RjYzU4YjQ5MzRlNDEzOGE5NDlhMTgyYTU0Y2ZmNWM=|1498653197|5eb2b7e7932423a4f49322a0d7b0d4087b5536a0\"; d_c0=\"AIBCCu2_-wuPTv6gpIWSY62GJJWv1Q6X7vM=|1498653198\"; _zap=5da260eb-bc7d-4ee6-8563-ca7267dc83a7; __utma=51854390.1081252107.1498653231.1498653231.1498653231.1; __utmc=51854390; __utmz=51854390.1498653231.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=51854390.000--|3=entry_date=20170613=1; l_n_c=1; l_cap_id=\"NzFiNzIxZjM0YTZkNGRmYWJkMGU3NzliZjljYWRmZGQ=|1498653211|9028a58183ea376bf5dcc3be0014a071a6fb0a16\"; n_c=1; _xsrf=77f2b8a9-9707-4117-b1e6-2876f8f8405a")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .addHeader("x-udid", "AIBCCu2_-wuPTv6gpIWSY62GJJWv1Q6X7vM=")
      .execute
    val result = resp.returnContent.asString(Charset.forName("GBK"))
    println(result)
  }
}
