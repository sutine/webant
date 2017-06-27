package org.webant.worker.http

import java.net.URI
import java.util.concurrent.TimeUnit

import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet, HttpPost}
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.{ConnectionSocketFactory, PlainConnectionSocketFactory}
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.{BasicCookieStore, HttpClientBuilder}
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager

import scala.util.control.NonFatal

/**
  * HttpClient 工具类
  */
object HttpClientUtils {

  val defaultClientBuilder = createClientBuilder()

  val defaultSslSocketFactory = createSslSocketFactory()

  def createClientBuilder() = HttpClientBuilder.create().setSSLSocketFactory(SSLConnectionSocketFactory.getSocketFactory)

  def createSslSocketFactory() = SSLConnectionSocketFactory.getSocketFactory

//  def createSslSocketFactory() = {
//    val sslContext = SSLContext.getInstance("TLS")
//    val trustManager = new X509TrustManager() {
//      override def getAcceptedIssuers: Array[X509Certificate] = null
//      override def checkClientTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}
//      override def checkServerTrusted(x509Certificates: Array[X509Certificate], s: String): Unit = {}
//    }
//    sslContext.init(null, Array[TrustManager](trustManager), null)
//    new SSLConnectionSocketFactory(sslContext)
//  }

  /**
    * HttpGet请求响应处理
    */
  def get[T](uri: URI, clientBuilder: HttpClientBuilder)(requestHandler: HttpGet => Unit)(responseHandler: CloseableHttpResponse => T): Either[Throwable, T] = {
    try {
//      if (uri.getScheme == "https")
//        clientBuilder.setSSLSocketFactory(defaultSslSocketFactory)
      val client = clientBuilder.build()
      val request = new HttpGet(uri)
      requestHandler(request)

      val response = client.execute(request)
      Right(responseHandler(response))
    }
    catch {
      case NonFatal(e) => Left(e)
    }
  }

  def get[T](uri: URI)(requestHandler: HttpGet => Unit)(responseHandler: CloseableHttpResponse => T): Either[Throwable, T] =
    get[T](uri, defaultClientBuilder)(requestHandler)(responseHandler)

  /**
    * HttpGet请求响应处理
    */
  def get[T](uri: String, clientBuilder: HttpClientBuilder)(requestHandler: HttpGet => Unit)(responseHandler: CloseableHttpResponse => T): Either[Throwable, T] =
    get[T](URI.create(uri), clientBuilder)(requestHandler)(responseHandler)

  def get[T](uri: String)(requestHandler: HttpGet => Unit)(responseHandler: CloseableHttpResponse => T): Either[Throwable, T] =
    get[T](URI.create(uri))(requestHandler)(responseHandler)

  def post[T](uri: URI, clientBuilder: HttpClientBuilder)(requestHandler: HttpPost => Unit)(responseHandler: CloseableHttpResponse => T): Either[Throwable, T] = {
    try {
//      if (uri.getScheme == "https")
//        clientBuilder.setSSLSocketFactory(defaultSslSocketFactory)
      val client = clientBuilder.build()
      val request = new HttpPost(uri)
      requestHandler(request)

      val response = client.execute(request)
      Right(responseHandler(response))
    }
    catch {
      case NonFatal(e) => Left(e)
    }
  }

  def post[T](uri: URI)(requestHandler: HttpPost => Unit)(responseHandler: CloseableHttpResponse => T): Either[Throwable, T] =
    post[T](uri, defaultClientBuilder)(requestHandler)(responseHandler)

  def post[T](uri: String, clientBuilder: HttpClientBuilder)(requestHandler: HttpPost => Unit)(responseHandler: CloseableHttpResponse => T): Either[Throwable, T] =
    post[T](URI.create(uri), clientBuilder)(requestHandler)(responseHandler)

  def post[T](uri: String)(requestHandler: HttpPost => Unit)(responseHandler: CloseableHttpResponse => T): Either[Throwable, T] =
    post[T](URI.create(uri))(requestHandler)(responseHandler)

  private def initHttpClientBuilder(): HttpClientBuilder = {
    val timeout = 30 * 1000
    val requestConfig = RequestConfig.custom()
      .setConnectTimeout(timeout)
      .setConnectionRequestTimeout(timeout)
      .setSocketTimeout(timeout)
      .build()

    val connectionManager = new PoolingHttpClientConnectionManager(
      RegistryBuilder.create[ConnectionSocketFactory]
        .register("http", PlainConnectionSocketFactory.getSocketFactory)
        .register("https", SSLConnectionSocketFactory.getSocketFactory).build,
      null,
      null,
      null,
      -1,
      TimeUnit.MILLISECONDS)

    val cookieStore = new BasicCookieStore()

    val session = HttpClientUtils.createClientBuilder()
      .setDefaultRequestConfig(requestConfig)
      .setConnectionManager(connectionManager)
      .setDefaultCookieStore(cookieStore)
      .disableRedirectHandling()

    session
  }
}
