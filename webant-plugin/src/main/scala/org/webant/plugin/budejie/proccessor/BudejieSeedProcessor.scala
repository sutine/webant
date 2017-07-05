package org.webant.plugin.budejie.proccessor

import org.apache.commons.lang3.StringUtils
import org.webant.plugin.budejie.data.BudejieDetailData
import org.webant.worker.processor.HtmlPageProcessor

import scala.collection.JavaConverters._

class BudejieSeedProcessor extends HtmlPageProcessor[BudejieDetailData] {
  regex = "http://www.budejie.com/"

  override def links(): Iterable[String] = {
    val pageNums = doc.select(".m-page a").asScala.map(a => a.attr("href")).filter(StringUtils.isNumeric(_))

    pageNums.map(pageNum =>
      s"http://www.budejie.com/$pageNum")
  }
}
