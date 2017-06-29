package org.webant.worker.store

import java.net.{InetAddress, UnknownHostException}

import org.apache.commons.collections.MapUtils
import org.apache.commons.lang3.StringUtils
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.support.WriteRequest
import org.elasticsearch.action.update.UpdateRequest
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.xcontent.XContentFactory
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.webant.commons.utils.JsonUtils
import org.webant.worker.http.HttpDataEntity

class ElasticSearchStore[T <: HttpDataEntity] extends IStore[T] {

  private var client: Client = _
  private var index: String = _
  private var `type`: String = "default"

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("host") || !params.containsKey("port"))
      return false

    val clusterName = MapUtils.getString(params, "clusterName")
    val host = MapUtils.getString(params, "host")
    val port = MapUtils.getInteger(params, "port", 9300)
    index = MapUtils.getString(params, "index")
    `type` = MapUtils.getString(params, "type", "default")

    val settings = Settings.builder
      .put("cluster.name", clusterName)
      .put("client.transport.ping_timeout", "10s")
      .put("transport.ping_schedule", "5s")
      .build

    try
      client = new PreBuiltTransportClient(settings)
        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port))
    catch {
      case e: UnknownHostException =>
        e.printStackTrace()
        return false
    }

    true
  }

  override def save(data: T): Int = {
    0
  }

  override def upsert(data: T): Int = {
    require(data != null)
    upsertEs(data.id, null, JsonUtils.toJson(data), isRefresh = false)
  }

  private def upsertEs(id: String, parentId: String, json: String, isRefresh: Boolean): Int = {
    val indexRequest = new IndexRequest(index, `type`)
      .source(json, XContentFactory.xContentType(json))
    if (isRefresh) indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
    if (StringUtils.isNotBlank(parentId)) indexRequest.parent(parentId)
    if (StringUtils.isNotBlank(id)) indexRequest.id(id)

    val updateRequest = new UpdateRequest(index, `type`, id)
      .doc(json, XContentFactory.xContentType(json))
      .upsert(indexRequest)
    if (isRefresh) updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)

    val version = client.update(updateRequest).get.getVersion
    if (version > 0) 1 else 0
  }

}
