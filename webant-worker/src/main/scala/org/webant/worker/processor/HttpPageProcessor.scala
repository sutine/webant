package org.webant.worker.processor

import java.util.Date

import org.apache.commons.lang3.StringUtils
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.apache.http.util.EntityUtils
import org.apache.log4j.LogManager
import org.webant.commons.utils.Retry
import org.webant.worker.config.HttpConfig
import org.webant.worker.http.{HttpDataEntity, HttpResponse}
import org.webant.worker.store.IStore

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

class HttpPageProcessor[T <: HttpDataEntity : ClassTag] {
  private val logger = LogManager.getLogger(classOf[HttpPageProcessor[HttpDataEntity]])

  var regex: String = _
  var stores: Iterable[IStore[HttpDataEntity]] = _
  var http: HttpConfig = _

  def accept(url: String): Boolean = url.matches(regex)

  final def process(url: String, body: String): HttpResponse[T] = {
    var response = new HttpResponse[T]

    try {
      response = fetch(url, body)

      if (response == null || response.fail || StringUtils.isBlank(response.content))
        return response

      parse(response.content)

      response.list = list()
      response.list.foreach(data => {
        data.srcUrl = url
        data.crawlTime = new Date()
      })

      response.data = data()
      if (response.data != null) {
        response.data.srcUrl = url
        response.data.crawlTime = new Date
      }

      response.links = links()

      write(response.list)
      write(response.data)
    } catch {
      case e: Exception =>
        response.data = null.asInstanceOf[T]
        response.links = Iterable.empty
        e.printStackTrace()
    }

    response
  }

  private def fetch(url: String, body: String): HttpResponse[T] = {
    var request: Request = null
    var encoding = "UTF-8"
    var retryTimes = 0

    if (http != null) {
      if ("GET".equalsIgnoreCase(http.method))
        request = org.apache.http.client.fluent.Request.Get(url)
      else if ("POST".equalsIgnoreCase(http.method))
        request = org.apache.http.client.fluent.Request.Post(url)
      else if ("PUT".equalsIgnoreCase(http.method))
        request = org.apache.http.client.fluent.Request.Put(url)
      else if ("DELETE".equalsIgnoreCase(http.method))
        request = org.apache.http.client.fluent.Request.Delete(url)
      else if ("OPTIONS".equalsIgnoreCase(http.method))
        request = org.apache.http.client.fluent.Request.Options(url)
      else if ("HEAD".equalsIgnoreCase(http.method))
        request = org.apache.http.client.fluent.Request.Head(url)
      else if ("PATCH".equalsIgnoreCase(http.method))
        request = org.apache.http.client.fluent.Request.Patch(url)
      else if ("TRACE".equalsIgnoreCase(http.method))
        request = org.apache.http.client.fluent.Request.Trace(url)
      else
        request = org.apache.http.client.fluent.Request.Post(url)

      if (http.socketTimeout >= 0)
        request.socketTimeout(http.socketTimeout)
      if (http.connectTimeout >= 0)
        request.connectTimeout(http.connectTimeout)

      if (StringUtils.isNotBlank(http.encoding))
        encoding = http.encoding

      if (StringUtils.isNotBlank(http.body))
        request.bodyString(http.body, ContentType.create(http.contentType))

      retryTimes = if (http.retryTimes < 0 || http.retryTimes > 10) 3 else http.retryTimes

      http.headers.asScala.foreach(item => {
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

  protected def list(): Iterable[T] = Iterable.empty

  protected def links(): Iterable[String] = Iterable.empty

  private def write(list: Iterable[T]): Int = {
    var affectRowCount = 0
    if (stores == null || stores.isEmpty || list == null || list.isEmpty) return affectRowCount
    try {
      affectRowCount = stores.map(store => store.upsert(list)).sum
    } catch {
      case e: Exception =>
        logger.error(e.getMessage())
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
        logger.error(e.getMessage())
    }

    affectRowCount
  }
}