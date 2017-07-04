package org.webant.plugin.budejie.data

import java.util.Date

import org.webant.commons.elasticsearch.annotation.{Index, Type}
import org.webant.commons.entity.HttpDataEntity

import scala.beans.BeanProperty

@Index("webant")
@Type("data")
class BudejieDetailData extends HttpDataEntity {
  @BeanProperty
  var userName: String = _
  @BeanProperty
  var avatarUrl: String = _
  @BeanProperty
  var profileUrl: String = _
  @BeanProperty
  var title: String = _
  @BeanProperty
  var content: String = _
  @BeanProperty
  var publishTime: Date = _
  @BeanProperty
  var imgUrl: String = _
  @BeanProperty
  var imgWith: Integer = _
  @BeanProperty
  var imgHeight: Integer = _
  @BeanProperty
  var likeNum: Integer = _
  @BeanProperty
  var hateNum: Integer = _
  @BeanProperty
  var commentNum: Integer = _
  @BeanProperty
  var funType: String = _
}
