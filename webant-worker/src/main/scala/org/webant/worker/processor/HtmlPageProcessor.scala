package org.webant.worker.processor

import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.webant.commons.entity.HttpDataEntity

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

class HtmlPageProcessor[T <: HttpDataEntity : ClassTag] extends HttpPageProcessor[T] {
  protected var doc: Document = _

  protected override def parse(content: String): Unit = {
    doc = Jsoup.parse(content)
  }

  protected override def links(): Iterable[String] = {
    doc.select("a").asScala.map(_.absUrl("href")).filter(StringUtils.isNotBlank(_)).distinct
  }

}
