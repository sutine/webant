package org.webant.plugin.toutiao.data

import org.webant.commons.entity.HttpDataEntity

import scala.beans.BeanProperty

class ToutiaoAccountData extends HttpDataEntity {

  @BeanProperty
  var accountId: String = _
}
