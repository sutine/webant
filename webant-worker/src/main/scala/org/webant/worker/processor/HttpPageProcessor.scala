package org.webant.worker.processor

import java.net.URL
import java.util
import java.util.Date

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.SiteConfig.HttpConfig
import org.webant.commons.entity.{HttpDataEntity, Link}
import org.webant.commons.store.IStore
import org.webant.commons.utils.Retry
import org.webant.worker.http.HttpResponse

import scala.collection.JavaConverters._

class HttpPageProcessor[T <: HttpDataEntity] {
  private val logger = LogManager.getLogger(classOf[HttpPageProcessor[HttpDataEntity]])

  var regex: String = _
  var stores: Iterable[IStore[HttpDataEntity]] = _
  var http: HttpConfig = _
  var url: URL = _

  def accept(url: String): Boolean = url.matches(regex)

  final def process(link: Link): HttpResponse[T] = {
    var response = new HttpResponse[T]

    try {
      response = fetch(link.getUrl, link.getBody)

      if (response == null || response.fail || StringUtils.isBlank(response.content))
        return response

      url = new URL(link.getUrl)
      parse(response.content)

      response.list = list()
      response.list.asScala.foreach(data => {
        data.id = DigestUtils.md5Hex(s"${link.getTaskId}_${link.getSiteId}_${data.srcId}")
        data.srcUrl = link.getUrl
        data.taskId = link.getTaskId
        data.siteId = link.getSiteId
        data.crawlTime = new Date()
      })

      response.data = data()
      if (response.data != null) {
        response.data.id = DigestUtils.md5Hex(s"${link.getTaskId}_${link.getSiteId}_${response.data.srcId}")
        response.data.srcUrl = link.getUrl
        response.data.taskId = link.getTaskId
        response.data.siteId = link.getSiteId
        response.data.crawlTime = new Date
      }

      response.links = links()

      write(response.list)
      write(response.data)
    } catch {
      case e: Exception =>
        response.data = null.asInstanceOf[T]
        response.links = new util.LinkedList[String]()
        e.printStackTrace()
    }

    response
  }

  private def fetch(url: String, body: String): HttpResponse[T] = {
    var request: Request = null
    var encoding = "UTF-8"
    var retryTimes = 0

    if (http != null) {
      if ("GET".equalsIgnoreCase(http.getMethod))
        request = org.apache.http.client.fluent.Request.Get(url)
      else if ("POST".equalsIgnoreCase(http.getMethod))
        request = org.apache.http.client.fluent.Request.Post(url)
      else if ("PUT".equalsIgnoreCase(http.getMethod))
        request = org.apache.http.client.fluent.Request.Put(url)
      else if ("DELETE".equalsIgnoreCase(http.getMethod))
        request = org.apache.http.client.fluent.Request.Delete(url)
      else if ("OPTIONS".equalsIgnoreCase(http.getMethod))
        request = org.apache.http.client.fluent.Request.Options(url)
      else if ("HEAD".equalsIgnoreCase(http.getMethod))
        request = org.apache.http.client.fluent.Request.Head(url)
      else if ("PATCH".equalsIgnoreCase(http.getMethod))
        request = org.apache.http.client.fluent.Request.Patch(url)
      else if ("TRACE".equalsIgnoreCase(http.getMethod))
        request = org.apache.http.client.fluent.Request.Trace(url)
      else
        request = org.apache.http.client.fluent.Request.Post(url)

      if (http.getSocketTimeout >= 0)
        request.socketTimeout(http.getSocketTimeout)
      if (http.getConnectTimeout >= 0)
        request.connectTimeout(http.getConnectTimeout)

      if (StringUtils.isNotBlank(http.getEncoding))
        encoding = http.getEncoding

      if (StringUtils.isNotBlank(http.getBody))
        request.bodyString(http.getBody, ContentType.create(http.getContentType))

      retryTimes = if (http.getRetryTimes < 0 || http.getRetryTimes > 10) 3 else http.getRetryTimes

      http.getHeaders.asScala.foreach(item => {
        request.addHeader(item._1, item._2)
      })
    } else
      request = org.apache.http.client.fluent.Request.Post(url)

/*
    val httpClient = HttpUtils.createProxyHttpClient("175.6.254.244", 8080)
    //        CloseableHttpClient httpClient = HttpUtils.createHttpClient();
    import org.apache.http.client.fluent.Executor
    val executor = Executor.newInstance(httpClient)
    executor.execute(request)
*/

    val resp = Retry(retryTimes)(request.execute.returnResponse())

    val response = new HttpResponse[T]

    val status = resp.getStatusLine
    response.code = status.getStatusCode
    response.message = status.getReasonPhrase
    response.src = url
    response.content = EntityUtils.toString(resp.getEntity, encoding)

    response
  }

  protected def parse(content: String): Unit = Unit

  protected def data(): T = null.asInstanceOf[T]

  protected def list(): util.Collection[T] = new util.LinkedList[T]()

  protected def links(): util.Collection[String] = new util.LinkedList[String]()

  private def write(list: util.Collection[T]): Int = {
    var affectRowCount = 0
    if (stores == null || stores.isEmpty || list == null || list.isEmpty) return affectRowCount
    try {
      affectRowCount = stores.map(store => store.upsert(list.asScala)).sum
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
    }

    affectRowCount
  }

  private def write(data: T): Int = {
    var affectRowCount = 0
    if (stores == null || stores.isEmpty || data == null) return affectRowCount
    try {
      affectRowCount = stores.map(store => store.upsert(data)).sum
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
    }

    affectRowCount
  }
}