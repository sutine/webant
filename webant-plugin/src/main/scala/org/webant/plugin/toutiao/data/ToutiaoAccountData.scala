package org.webant.plugin.toutiao.data

import org.webant.worker.http.HttpDataEntity

import scala.beans.BeanProperty

class ToutiaoAccountData extends HttpDataEntity {

  @BeanProperty
  var accountId: String = _
}
