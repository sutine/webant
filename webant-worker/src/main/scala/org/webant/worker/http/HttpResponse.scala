package org.webant.worker.http

import java.util

import org.apache.http.HttpStatus
import org.webant.commons.entity.HttpDataEntity

class HttpResponse[T <: HttpDataEntity] {
  var code: Int = _
  var message: String = _
  var content: String = _
  var src: String = _
  var links: util.Collection[String] = _
  var data: T = _
  var list: util.Collection[T] = _

  def success: Boolean = {
    code == HttpStatus.SC_OK
  }

  def fail: Boolean = {
    code != HttpStatus.SC_OK
  }
}
