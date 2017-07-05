package org.webant.plugin.test.budejie

import java.io.IOException
import java.nio.charset.Charset

import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit

import scala.collection.JavaConverters._

class BudejieCrawlerTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
  }

  @Test
  def testListRegex(): Unit = {
    val regex = "http://www.budejie.com/\\d*"
    val url = "http://www.budejie.com/5"

    println(url.matches(regex))
  }

  @Test
  def testDetailRegex(): Unit = {
    val regex = "http://www.budejie.com/detail-\\d*.html"
    val url = "http://www.budejie.com/detail-25242571.html"

    println(url.matches(regex))
  }

  @Test
  def testSeed(): Unit = {
    val url = "http://www.budejie.com/"

    val resp = org.apache.http.client.fluent.Request.Get(url)
      //      .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
      .addHeader("Cache-Control", "max-age=0")
      .addHeader("Connection", "keep-alive")
      .addHeader("Cookie", "Hm_lvt_7c9f93d0379a9a7eb9fb60319911385f=1498616724; Hm_lpvt_7c9f93d0379a9a7eb9fb60319911385f=1499168114; tma=43102518.35365908.1498616723801.1498616723801.1499163828444.2; tmd=40.43102518.35365908.1498616723801.; _ga=GA1.2.815839684.1498616724; _gid=GA1.2.2061677000.1499163828; bfd_g=a7b902420a01410f00004010000020a15948e500")
      .addHeader("If-Modified-Since", "Tue, 04 Jul 2017 11:18:04 GMT")
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    val doc = Jsoup.parse(result)

    val pages = doc.select(".m-page a").asScala.map(a => a.attr("href")).filter(StringUtils.isNumeric(_))

    println(pages)
  }

  @Test
  def testList(): Unit = {
    val url = "http://www.budejie.com/2"

    val resp = org.apache.http.client.fluent.Request.Get(url)
      //      .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
      .addHeader("Cache-Control", "max-age=0")
      .addHeader("Connection", "keep-alive")
      .addHeader("Cookie", "Hm_lvt_7c9f93d0379a9a7eb9fb60319911385f=1498616724; Hm_lpvt_7c9f93d0379a9a7eb9fb60319911385f=1499168114; tma=43102518.35365908.1498616723801.1498616723801.1499163828444.2; tmd=40.43102518.35365908.1498616723801.; _ga=GA1.2.815839684.1498616724; _gid=GA1.2.2061677000.1499163828; bfd_g=a7b902420a01410f00004010000020a15948e500")
      .addHeader("If-Modified-Since", "Tue, 04 Jul 2017 11:18:04 GMT")
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    val doc = Jsoup.parse(result, url)

//    doc.setBaseUri("http://www.budejie.com/")
    val pages = doc.select("a").asScala.map(_.absUrl("href")).filter(StringUtils.isNotBlank(_)).distinct
//    val pages = doc.select("a[href]").asScala.map(_.attr("abs:href")).filter(StringUtils.isNotBlank(_))

    println(pages)
  }

  @Test
  @throws[IOException]
  def crawl() {
    val url = "http://www.budejie.com/detail-25625224.html"

    val resp = org.apache.http.client.fluent.Request.Get(url)
//      .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED)
      .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
      .addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8")
      .addHeader("Cache-Control", "max-age=0")
      .addHeader("Connection", "keep-alive")
      .addHeader("Cookie", "Hm_lvt_7c9f93d0379a9a7eb9fb60319911385f=1498616724; Hm_lpvt_7c9f93d0379a9a7eb9fb60319911385f=1499168114; tma=43102518.35365908.1498616723801.1498616723801.1499163828444.2; tmd=40.43102518.35365908.1498616723801.; _ga=GA1.2.815839684.1498616724; _gid=GA1.2.2061677000.1499163828; bfd_g=a7b902420a01410f00004010000020a15948e500")
      .addHeader("If-Modified-Since", "Tue, 04 Jul 2017 11:18:04 GMT")
      .addHeader("Upgrade-Insecure-Requests", "1")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    val doc = Jsoup.parse(result, url)

    val id = doc.select("#hidPid").attr("value")
    val profileUrl = doc.select(".j-list-user .u-img a").attr("href")
    val avatarUrl = doc.select(".j-list-user .u-img a img").attr("data-original")
    val userName = doc.select(".j-list-user .u-txt a").text()
    val publishTime = doc.select(".j-list-user .u-txt .u-time").text()
    val title = doc.select(".j-r-list-c .j-r-list-c-desc").text()
    val imgUrl = doc.select(".content-text img").attr("src")
    val likeNum = doc.select(".j-r-list-tool .j-r-list-tool-l-up").text()
    val hateNum = doc.select(".j-r-list-tool .j-r-list-tool-l-down").text()
    val shareNum = doc.select(".j-r-list-tool .j-r-list-tool-ct-share-c").text().replace(" ", "")
    val commentNum = doc.select(".j-r-list-tool .j-r-list-tool-l-comment").text()

    val pages = doc.select("a").asScala.map(a => a.attr("href")).filter(StringUtils.isNumeric(_))

    System.out.println(title)
  }
}
