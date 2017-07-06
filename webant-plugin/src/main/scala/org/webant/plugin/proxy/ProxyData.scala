package org.webant.plugin.proxy

import java.util.Date

import org.webant.commons.elasticsearch.annotation.{Index, Type}
import org.webant.commons.entity.HttpDataEntity

@Index("webant")
@Type("proxy")
class ProxyData extends HttpDataEntity {
  var ip: String = _
  var port: Integer = _
  var anonymousLevel: String = _
  var location: String = _
  var speed: Long = _
  var detectTime: Date = _
  var proxyType: String = _
}
