package org.webant.worker.config

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.commons.utils.JsonUtils
import org.webant.worker.store.StoreFactory

class SiteConfigBuilder {
  private val logger = LogManager.getLogger(classOf[SiteConfigBuilder])
  private var siteConfig = new SiteConfig

  def loadSiteConfig(path: String): SiteConfigBuilder = {
    require(StringUtils.isNotBlank(path), "site config path can not be empty.")
    val file = new File(path)
    require(file.exists(), "site config does not exists.")
    require(!file.isDirectory, "site config can not be a directory.")
    val content = FileUtils.readFileToString(file, "UTF-8")

    if (StringUtils.isNotBlank(content)) {
      siteConfig = JsonUtils.fromJson(content, classOf[SiteConfig])

      siteConfig.processors.foreach(StoreFactory.load)
      logger.info(s"loading site config ${siteConfig.id}(${siteConfig.name}) from ${file.getAbsolutePath}")
    }

    this
  }

  def build(): SiteConfig = {
    require(StringUtils.isNotBlank(siteConfig.id), "id can not be empty!")
    require(siteConfig.seeds != null && siteConfig.seeds.nonEmpty, "seeds can not be empty!")
    require(siteConfig.processors != null && siteConfig.processors.nonEmpty, "processors can not be empty!")

    siteConfig
  }

  def id(id: String): SiteConfigBuilder = {
    siteConfig.id = id
    this
  }

  def name(name: String): SiteConfigBuilder = {
    siteConfig.name = name
    this
  }

  def description(description: String): SiteConfigBuilder = {
    siteConfig.description = description
    this
  }

  def seeds(seeds: Array[String]): SiteConfigBuilder = {
    siteConfig.seeds = seeds
    this
  }

  def priority(priority: Integer): SiteConfigBuilder = {
    siteConfig.priority = priority
    this
  }

  def interval(interval: Long): SiteConfigBuilder = {
    siteConfig.interval = interval
    this
  }

  def http(http: HttpConfig): SiteConfigBuilder = {
    siteConfig.http = http
    this
  }

  def linkProvider(linkProvider: LinkProvider): SiteConfigBuilder = {
    siteConfig.linkProvider = linkProvider
    this
  }

  def processors(processors: Array[ProcessorConfig]): SiteConfigBuilder = {
    siteConfig.processors = processors
    this
  }
}

class HttpConfigBuilder {
  private val httpConfig = new HttpConfig

  var method: String = _
  var connectTimeout: Int = _
  var socketTimeout: Int = _
  var encoding: String = _
  var retryTimes: Int = _
  var cycleRetryTimes: Int = _
  var contentType: String = _
  var proxy: Boolean = _
  var headers: java.util.Map[String, String] = _

  def build(): HttpConfig = {
    httpConfig
  }

  def method(method: String): HttpConfigBuilder = {
    httpConfig.method = method
    this
  }

  def connectTimeout(connectTimeout: Integer): HttpConfigBuilder = {
    httpConfig.connectTimeout = connectTimeout
    this
  }

  def socketTimeout(socketTimeout: Integer): HttpConfigBuilder = {
    httpConfig.socketTimeout = socketTimeout
    this
  }

  def encoding(encoding: String): HttpConfigBuilder = {
    httpConfig.encoding = encoding
    this
  }

  def retryTimes(retryTimes: Integer): HttpConfigBuilder = {
    httpConfig.retryTimes = retryTimes
    this
  }

  def cycleRetryTimes(cycleRetryTimes: Integer): HttpConfigBuilder = {
    httpConfig.cycleRetryTimes = cycleRetryTimes
    this
  }

  def contentType(contentType: String): HttpConfigBuilder = {
    httpConfig.contentType = contentType
    this
  }

  def proxy(proxy: Boolean): HttpConfigBuilder = {
    httpConfig.proxy = proxy
    this
  }

  def headers(headers: java.util.Map[String, String]): HttpConfigBuilder = {
    httpConfig.headers = headers
    this
  }
}

class PageProcessorBuilder {
  private val processorConfig = new ProcessorConfig
  var regex: String = _
  var http: HttpConfig = _
  var className: String = _
  var store: Array[java.util.Map[String, String]] = _

  def build(): ProcessorConfig = {
    require(StringUtils.isNotBlank(processorConfig.regex))

    processorConfig
  }

  def http(http: HttpConfig): PageProcessorBuilder = {
    processorConfig.http = http
    this
  }

  def regex(regex: String): PageProcessorBuilder = {
    processorConfig.regex = regex
    this
  }

  def className(className: String): PageProcessorBuilder = {
    processorConfig.className = className
    this
  }

  def store(store: Array[StoreProvider]): PageProcessorBuilder = {
    processorConfig.store = store
    this
  }
}

class LinkProviderBuilder {
  private val linkProvider = new LinkProvider
  var className: String = _
  var params: java.util.Map[String, Object] = _

  def build(): LinkProvider = {
    require(StringUtils.isNotBlank(linkProvider.className))

    linkProvider
  }

  def className(className: String): LinkProviderBuilder = {
    linkProvider.className = className
    this
  }

  def params(params: java.util.Map[String, Object]): LinkProviderBuilder = {
    linkProvider.params = params
    this
  }
}
