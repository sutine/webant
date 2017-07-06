package org.webant.commons.entity

import java.util.Date

import org.webant.commons.annotation.{DocId, DocParentId}

import scala.beans.BeanProperty

class HttpDataEntity extends Serializable {
  @DocId
  @DocParentId("parentId")
  @BeanProperty
  var id: String = _
  @BeanProperty
  var source: String = _
  @BeanProperty
  var srcId: String = _
  @BeanProperty
  var srcUrl: String = _
  @BeanProperty
  var taskId: String = _
  @BeanProperty
  var siteId: String = _
  @BeanProperty
  var crawlTime: Date = _
  @BeanProperty
  var dataVersion: java.lang.Integer = 1
  @BeanProperty
  var dataCreateTime: Date = new Date()
  @BeanProperty
  var dataUpdateTime: Date = new Date()
  @BeanProperty
  var dataDeleteTime: Date = _
}
