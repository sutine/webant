package org.webant.plugin.toutiao.data

import org.webant.commons.entity.HttpDataEntity

import scala.beans.BeanProperty

class ToutiaoArticleData extends HttpDataEntity {

  @BeanProperty
  var toutiaoId: String = _
}
