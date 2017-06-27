package org.webant.plugin.toutiao.processor

import org.webant.plugin.toutiao.data.{ToutiaoAccountData, ToutiaoArticleData}
import org.webant.worker.processor.HtmlPageProcessor

class ToutiaoAccountProcessor extends HtmlPageProcessor[ToutiaoAccountData] {
  regex = "http://www.toutiao.com/c/user/\\d*/#mid=\\d*"

  override protected def data(): ToutiaoAccountData = {
    new ToutiaoAccountData
  }

  override protected def list(): Iterable[ToutiaoAccountData] = {
    Iterable.empty
  }
}
