package org.webant.plugin.toutiao.processor

import org.webant.plugin.toutiao.data.ToutiaoArticleDetailData
import org.webant.worker.processor.HtmlPageProcessor

class ToutiaoArticleDetailProcessor extends HtmlPageProcessor[ToutiaoArticleDetailData] {
  regex = "http://www.toutiao.com/\\w*\\d*/"

  override protected def data(): ToutiaoArticleDetailData = {
    new ToutiaoArticleDetailData
  }
}
