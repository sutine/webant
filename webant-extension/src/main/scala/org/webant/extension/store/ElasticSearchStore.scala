package org.webant.extension.store

import org.apache.commons.collections.MapUtils
import org.webant.commons.entity.HttpDataEntity
import org.webant.extension.elasticsearch.ElasticSearchUtils
import org.webant.commons.store.IStore

import scala.collection.JavaConverters._

class ElasticSearchStore[T <: HttpDataEntity] extends IStore[T] {

  private val esUtils: ElasticSearchUtils[T] = new ElasticSearchUtils[T]

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("host") || !params.containsKey("port"))
      return false

    val clusterName = MapUtils.getString(params, "clusterName")
    val host = MapUtils.getString(params, "host")
    val port = MapUtils.getInteger(params, "port", 9300)

    esUtils.init(clusterName, host, port)
  }

  override def save(data: T): Int = {
    0
  }

  override def upsert(list: Iterable[T]): Int = {
    if (list == null || list.isEmpty) return 0
    esUtils.upsert(list.toList.asJava, false)
  }

  override def upsert(data: T): Int = {
    if (data == null) return 0
    esUtils.upsert(data, false)
  }
}
