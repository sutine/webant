package org.webant.plugin.toutiao.data

import org.webant.worker.http.HttpDataEntity

import scala.beans.BeanProperty

class ToutiaoArticleData extends HttpDataEntity {

  @BeanProperty
  var toutiaoId: String = _
}
