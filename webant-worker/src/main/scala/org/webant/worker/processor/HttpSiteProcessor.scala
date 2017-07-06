package org.webant.worker.processor

import java.net.SocketTimeoutException
import java.util.Date

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DurationFormatUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.{HttpDataEntity, Link}
import org.webant.worker.config.{ProcessorConfig, SiteConfig}
import org.webant.worker.exception.{HttpRequestException, ParseContentException}
import org.webant.worker.http.HttpResponse
import org.webant.worker.link.ILinkProvider
import org.webant.worker.store.StoreFactory

import scala.util.control.NonFatal

class HttpSiteProcessor(linkProvider: ILinkProvider, siteConfig: SiteConfig) extends ISiteProcessor {
  private val logger = LogManager.getLogger(classOf[HttpSiteProcessor])
  private val processors = getProcessors(siteConfig.processors)

  override def accept(url: String): Boolean = {
    if (siteConfig.seeds.contains(url))
      return true
    processors.exists(_.accept(url))
  }

  final def process(link: Link): HttpResponse[HttpDataEntity] = {
    var resp = new HttpResponse[HttpDataEntity]

    if (StringUtils.isBlank(link.getUrl))
      return resp

    if (!accept(link.getUrl))
      return resp

    val start = System.currentTimeMillis()
    try {
      val processor = getProcessor(link.getUrl)
      if (processor == null)
        return resp

      resp = processor.process(link)

      if (resp != null && resp.links != null) {
        // save success link
        link.setStatus(Link.LINK_STATUS_SUCCESS)
        linkProvider.write(link)
        // save accept links
        val accepts = resp.links.filter(accept).map(url => new Link(DigestUtils.md5Hex(url), link.getTaskId, link.getSiteId, url, link.getUrl, new Date()))
        if (accepts.nonEmpty)
          linkProvider.write(accepts)

        logger.info(s"[${link.getTaskId}, ${link.getSiteId}]. crawl success. link: ${link.getUrl}, id: ${if (resp.data == null) "" else resp.data.id}, " +
          s"srcId: ${if (resp.data == null) "" else resp.data.srcId}, data list number: ${resp.list.size}, link number: ${accepts.size}, " +
          s"elapse: ${DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, "HH:mm:ss.SSS")}.")
      } else {
        link.setStatus(Link.LINK_STATUS_FAIL)
        linkProvider.write(link)

        logger.error(s"crawl failed. link: ${link.getUrl}, code: ${resp.code}, message: ${resp.message}, " +
          s"elapse: ${DurationFormatUtils.formatDuration(System.currentTimeMillis() - start, "HH:mm:ss.SSS")}.")
      }
    }
    catch {
      case NonFatal(e: HttpRequestException) =>
        logger.error(s"crawl url failed! url: ${link.getUrl}. ${e.getMessage}")
      case NonFatal(e: ParseContentException) =>
        logger.error(s"parse content failed! url: ${link.getUrl}. ${e.getMessage}")
      case NonFatal(e: SocketTimeoutException) =>
        logger.error(s"connect to server time out, crawl failed! url: ${link.getUrl}. ${e.getMessage}")
      case e: Exception =>
        logger.error(s"anything error, crawl failed! url: ${link.getUrl}. ${e.getMessage}")
    }
    finally {
      //      close()
    }

    resp
  }

  private def getProcessors(processorConfig: Iterable[ProcessorConfig]): Iterable[HttpPageProcessor[HttpDataEntity]] = {
    require(processorConfig != null && processorConfig.nonEmpty)
    processorConfig.map(config => {
      val processor =
        if (StringUtils.isBlank(config.className))
          new HtmlPageProcessor[HttpDataEntity]
        else
          Class.forName(config.className).newInstance().asInstanceOf[HttpPageProcessor[HttpDataEntity]]

      if (StringUtils.isNotBlank(config.regex))
        processor.regex = config.regex

      processor.http = if (config.http != null) config.http else siteConfig.http
      processor.stores = StoreFactory.getStores(config.className)

      processor
    })
  }

  private def getProcessor(url: String): HttpPageProcessor[HttpDataEntity] = {
    processors.find(_.accept(url)).orNull
  }
}
