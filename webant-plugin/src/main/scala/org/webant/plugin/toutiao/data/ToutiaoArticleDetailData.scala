package org.webant.plugin.toutiao.data

import org.webant.commons.entity.HttpDataEntity

import scala.beans.BeanProperty

class ToutiaoArticleDetailData extends HttpDataEntity {

  @BeanProperty
  var articleId: String = _
}
