package org.webant.worker.processor

import org.apache.commons.lang3.StringUtils
import org.webant.worker.http.HttpDataEntity

import scala.reflect.ClassTag

class JsonPageProcessor[T <: HttpDataEntity : ClassTag] extends HttpPageProcessor[T] {
  protected var content: String = _

  protected override def parse(content: String): Unit = {
    this.content = content
  }

  protected def isJson(content: String): Boolean = {
    StringUtils.isNotBlank(content) && ((content.startsWith("{") && content.endsWith("}")) || (content.startsWith("[") && content.endsWith("]")))
  }
}
