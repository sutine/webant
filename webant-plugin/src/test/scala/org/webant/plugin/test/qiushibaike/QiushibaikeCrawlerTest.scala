package org.webant.plugin.test.qiushibaike

import java.io.IOException
import java.nio.charset.Charset

import org.apache.commons.lang3.StringUtils
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
    val regex = "https://www.qiushibaike.com/article/\\d*"
    val url = "https://www.qiushibaike.com/article/118986739"

    println(url.matches(regex))
  }

  @Test
  @throws[IOException]
  def testCrawlUser() {
    val url = "https://www.qiushibaike.com/users/20524321/articles/"

    val resp = org.apache.http.client.fluent.Request.Get(url)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate, br")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
      .addHeader("Cache-Control", "max-age=0")
      .addHeader("Connection", "keep-alive")
      .addHeader("Cookie", "_xsrf=2|e112fdcf|96807aea7c64004411315ab1b484fe4b|1499159470; _qqq_uuid_=\"2|1:0|10:1499159470|10:_qqq_uuid_|56:NjM1MjdjYzgwNGI1MmVkN2MxYzFmMGRiNWExNGI4ZjZhMmQwNTVlZg==|2b431da262fc63a66aba8776a565d9a7f6c8154695bf48d404f716aab3bd8ef8\"; callback_url=/new4/session%3Fsrc%3Dwx%26code%3D003yx7Ki179dny09o3Ki1uJjKi1yx7KB%26state%3D; __cur_art_index=9281; _ga=GA1.2.1637544603.1499159508; _gid=GA1.2.1389000090.1499159508; Hm_lvt_2670efbdd59c7e3ed3749b458cafaa37=1499159508; Hm_lpvt_2670efbdd59c7e3ed3749b458cafaa37=1499241085")
      .addHeader("If-None-Match", "214c5dee29e2857db74399d3101ba0167088461c")
      .addHeader("Host", "www.qiushibaike.com")
      .addHeader("Referer", url)
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    val doc = Jsoup.parse(result, url)
    val profileUrl = doc.select(".newArticleHead a").first().absUrl("href")
    val avatarUrl = doc.select(".newArticleHead img").attr("src")
    val userName = doc.select(".newArticleHead .touch-user-name-a").text()
    val title = doc.select(".content-text").text()
    val imgUrl = doc.select(".content-text img").attr("src")
    val likeNum = doc.select(".article_info .laugh-comment").attr("data-votes")
    val commentNum = doc.select(".article_info .comments").text().split(" ")(0)
    println(title)
  }

  @Test
  @throws[IOException]
  def crawl() {
    val url = "https://www.qiushibaike.com/article/118790265"

    val resp = org.apache.http.client.fluent.Request.Get(url)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate, br")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
      .addHeader("Cache-Control", "max-age=0")
      .addHeader("Connection", "keep-alive")
      .addHeader("Cookie", "_xsrf=2|e112fdcf|96807aea7c64004411315ab1b484fe4b|1499159470; _qqq_uuid_=\"2|1:0|10:1499159470|10:_qqq_uuid_|56:NjM1MjdjYzgwNGI1MmVkN2MxYzFmMGRiNWExNGI4ZjZhMmQwNTVlZg==|2b431da262fc63a66aba8776a565d9a7f6c8154695bf48d404f716aab3bd8ef8\"; callback_url=/new4/session%3Fsrc%3Dwx%26code%3D003yx7Ki179dny09o3Ki1uJjKi1yx7KB%26state%3D; __cur_art_index=9281; _ga=GA1.2.1637544603.1499159508; _gid=GA1.2.1389000090.1499159508; Hm_lvt_2670efbdd59c7e3ed3749b458cafaa37=1499159508; Hm_lpvt_2670efbdd59c7e3ed3749b458cafaa37=1499241085")
      .addHeader("If-None-Match", "214c5dee29e2857db74399d3101ba0167088461c")
      .addHeader("Host", "www.qiushibaike.com")
      .addHeader("Referer", url)
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    val doc = Jsoup.parse(result, url)
    val srcId = StringUtils.substringAfter(url, "/article/")
    val content = doc.select("#single-next-link .content").text()
    val imgUrl = doc.select("#single-next-link img").first().absUrl("src")
    val title = doc.select("#single-next-link img").first().attr("alt")
    val profileUrl = doc.select(s"#qiushi_tag_$srcId a").first().absUrl("href")
    val avatarUrl = doc.select(s"#qiushi_tag_$srcId img").first().absUrl("src")
    val userName = doc.select(s"#qiushi_tag_$srcId img").first().attr("alt")
    val likeNum = doc.select(".stats-vote .number").text()
    val commentNum = doc.select(".stats-comments .number").text()
    val voteUp = doc.select(s"#vote-up-$srcId .number").text()
    val voteDown = doc.select(s"#vote-dn-$srcId .number").text()


//    val profileUrl = doc.select(".newArticleHead a").first().absUrl("href")
//    val avatarUrl = doc.select(".newArticleHead img").attr("src")
//    val userName = doc.select(".newArticleHead .touch-user-name-a").text()
//    val title = doc.select(".content-text").text()
//    val imgUrl = doc.select(".content-text img").attr("src")
    println(title)
  }
}
