package org.webant.plugin.test.toutiao

import java.io.IOException
import java.nio.charset.Charset

import org.apache.http.client.fluent.Response
import org.apache.http.entity.ContentType

object ToutiaoCrawler {

  def main(args: Array[String]) {
    getCrawl
  }

  @throws[IOException]
  private def crawl(start: Int, size: Int) {
    val url: String = "http://chuanbo.weiboyi.com/hworder/weixin/filterlist/source/all"
    val body: String = "web_csrf_token=58f07eaf60fa7&price_list=top%2Csecond%2Cother%2Csingle&start=" + start + "&limit=" + size
    val referer: String = "http://chuanbo.weiboyi.com/hworder/weixin/index?price_list=top%2Csecond%2Cother%2Csingle&start=" + start + "&limit=" + size
    val resp: Response = org.apache.http.client.fluent.Request.Post(url)
      .bodyString(body, ContentType.APPLICATION_FORM_URLENCODED)
      .addHeader("Proxy-Connection", "keep-alive")
      .addHeader("Pragma", "no-cache").addHeader("Cache-Control", "no-cache")
      .addHeader("Accept", "*/*").addHeader("Accept-Encoding", "gzip, deflate")
      .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
      .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
      .addHeader("X-Requested-With", "XMLHttpRequest")
      .addHeader("Cookie", "PHPSESSID=v5c8umqkqmm3l7504n2a22f676; TOUR_GUEST_RANK_DETAIL=1; Hm_lvt_b96f95878b55be2cf49fb3c099aea393=1492154811; Hm_lpvt_b96f95878b55be2cf49fb3c099aea393=1492156084; TRACK_USER_ID=422902; TRACK_IDENTIFY_AT=2017-04-14T07%3A48%3A07.747Z; TRACK_SESSION_ID=e9194002973b3ad500ca1342073935e4; TRACK_DETECTED=1.0.1; contactMain=1; Hm_lvt_29d7c655e7d1db886d67d7b9b3846aca=1492154543; Hm_lpvt_29d7c655e7d1db886d67d7b9b3846aca=1492156197; Hm_lvt_5ff3a7941ce54a1ba102742f48f181ab=1492155226,1492156194,1492156232; Hm_lpvt_5ff3a7941ce54a1ba102742f48f181ab=1492156232; _gscu_867320846=92154542syp83e15; _gscs_867320846=92154542ybw12i15|pv:19; _gscbrs_867320846=1")
      .addHeader("DNT", "1")
      .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
      .addHeader("Referer", referer)
      .addHeader("Origin", "http://chuanbo.weiboyi.com")
      .addHeader("Host", "chuanbo.weiboyi.com")
      .addHeader("Proxy-Connection", "keep-alive")
      .execute
    val result: String = resp.returnContent.asString(Charset.forName("GBK"))
    val fileName: String = "D:/cache/weiboyi/pages/data_" + start + "_" + size + ".json"
  }

  @throws[IOException]
  private def getCrawl() {
    val url = "http://www.toutiao.com/c/user/3478220103/#mid=3478721261"
    val referer = "http://www.toutiao.com/"
    val cookie = "uuid=\"w:14642531b6c04a51b79b3d5d84f20bee\"; UM_distinctid=15ab617be3a4f7-0549ae2cd99b8a-6a11157a-1fa400-15ab617be3b6a1; tt_webid=6389825691395507713; _ba=BA0.2-20170315-51d9e-ZqT8Iy0mz4o9ifpklLmp; utm_source=toutiao; csrftoken=271b16d4af8e2fe4de1a99ffb95f0087; CNZZDATA1259612802=1968230112-1487745221-%7C1492503393; _ga=GA1.2.747777808.1487747232; __tasessionId=emw3fn5tv1492504317360; sso_login_status=0; login_flag=88275e79eb5b8fe50e486971e6dc960b; sessionid=105fe5099b4a2cd777473d8249f7a153; sid_tt=105fe5099b4a2cd777473d8249f7a153; sid_guard=\"105fe5099b4a2cd777473d8249f7a153|1492506416|2592000|Thu\\054 18-May-2017 09:06:56 GMT\""
    val resp: Response = org.apache.http.client.fluent.Request.Get(url)
        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
        .addHeader("Accept-Encoding", "gzip, deflate, sdch")
        .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6")
        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
        .addHeader("Upgrade-Insecure-Requests", "1")
        .addHeader("Proxy-Connection", "keep-alive")
        .addHeader("Referer", referer)
        .addHeader("DNT", "1")
        .addHeader("Cookie", cookie)
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    val fileName: String = "D:/cache/weiboyi/pages/data"
  }
}
