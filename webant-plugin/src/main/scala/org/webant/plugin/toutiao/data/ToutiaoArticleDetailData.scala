package org.webant.plugin.toutiao.data

import org.webant.worker.http.HttpDataEntity

import scala.beans.BeanProperty

class ToutiaoArticleDetailData extends HttpDataEntity {

  @BeanProperty
  var articleId: String = _
}
