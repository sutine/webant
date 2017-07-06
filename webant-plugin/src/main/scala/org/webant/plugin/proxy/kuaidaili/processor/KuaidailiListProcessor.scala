package org.webant.plugin.proxy.kuaidaili.processor

import java.util.Date

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.time.DateUtils
import org.apache.log4j.LogManager
import org.webant.commons.utils.DateFormatUtils
import org.webant.plugin.proxy.ProxyData
import org.webant.worker.processor.HtmlPageProcessor

import scala.collection.JavaConverters._

class KuaidailiListProcessor extends HtmlPageProcessor[ProxyData] {
  private val logger = LogManager.getLogger(classOf[KuaidailiListProcessor])

  regex = "http://www.kuaidaili.com/free/?\\w*/?\\d*/?"

  override protected def list(): Iterable[ProxyData] = {
    val list = doc.select("#list tbody tr").asScala.map(tr => {
      val data = new ProxyData
      tr.select("td").asScala.foreach(td => {
        if (td.attr("data-title") == "IP")
          data.ip = td.text().trim
        if (td.attr("data-title") == "PORT") {
          try {
            data.port = td.text().trim.toInt
          } catch {
            case e: Exception =>
              data.port = 0
              logger.error(e.getMessage)
          }
        }
        if (td.attr("data-title") == "匿名度")
          data.anonymousLevel = td.text().trim
        if (td.attr("data-title") == "类型")
          data.proxyType = td.text().trim
        if (td.attr("data-title") == "位置")
          data.location = td.text().trim
        if (td.attr("data-title") == "响应速度") {
          try {
            data.speed = (StringUtils.remove(td.text().trim, "秒").toFloat * 1000).asInstanceOf[Long]
          } catch {
            case e: Exception =>
              data.speed = 0
              logger.error(e.getMessage)
          }
        }
        if (td.attr("data-title") == "最后验证时间")
          try {
            data.detectTime = DateUtils.parseDate(td.text().trim, DateFormatUtils.DATE_TIME_FORMAT)
          } catch {
            case e: Exception =>
              data.detectTime = new Date(0)
              logger.error(e.getMessage)
          }
      })

      data.srcId = data.ip + data.port
      data.source = "kuaidaili.com"
      data.crawlTime = new Date

      data
    })
    list
  }
}
