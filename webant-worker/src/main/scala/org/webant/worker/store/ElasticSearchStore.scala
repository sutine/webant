package org.webant.worker.store

import java.net.{InetAddress, UnknownHostException}

import org.apache.commons.collections.MapUtils
import org.apache.commons.lang3.StringUtils
import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.action.support.WriteRequest
import org.elasticsearch.action.update.UpdateRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.webant.commons.utils.JsonUtils
import org.webant.worker.http.HttpDataEntity

class ElasticSearchStore[T <: HttpDataEntity] extends IStore[T] {

  private var client: Client = _

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("host") || !params.containsKey("port"))
      return false

    val host = MapUtils.getString(params, "host")
    val port = MapUtils.getInteger(params, "port", 9300)

    val settings = Settings.builder
/*
    config.foreach{
      case (key, value) =>
        if (!"host".equalsIgnoreCase(key) && !"port".equalsIgnoreCase(key))
          settings.put(key, value)
    }
*/

    try
      client = new PreBuiltTransportClient(settings.build())
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
    upsertEs("test", "default", data.id, null, JsonUtils.toJson(data), isRefresh = false)
    1
  }

  private def saveEs(index: String, `type`: String, id: String, parentId: String, json: String, isRefresh: Boolean): IndexRequestBuilder = {
    if (StringUtils.isBlank(json)) return null
    //调用.setRefresh(true)设置实时索引，即该doc一提交马上能被搜索到
    val request = client.prepareIndex(index, `type`)
    request.setSource(json)
    if (isRefresh) request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
    if (StringUtils.isNotBlank(parentId)) request.setParent(parentId)
    if (StringUtils.isNotBlank(id)) request.setId(id)
//    request.execute.actionGet
    request
  }

  private def updateEs(index: String, `type`: String, id: String, parentId: String, json: String, isRefresh: Boolean): UpdateRequestBuilder = {
    if (StringUtils.isBlank(json)) return null
    //调用.setRefresh(true)设置实时索引，即该doc一提交马上能被搜索到
    val request = client.prepareUpdate(index, `type`, id)
    request.setDoc(json)
    if (isRefresh) request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
    if (StringUtils.isNotBlank(parentId)) request.setParent(parentId)
    if (StringUtils.isNotBlank(id)) request.setId(id)
//    request.execute.actionGet
    request
  }

  private def upsertEs(index: String, `type`: String, id: String, parentId: String, json: String, isRefresh: Boolean): Int = {
    if (StringUtils.isBlank(json)) return 0
    //调用.setRefresh(true)设置实时索引，即该doc一提交马上能被搜索到
    val request = updateEs(index, `type`, id, parentId, json, isRefresh)
    val indexRequest = saveEs(index, `type`, id, parentId, json, isRefresh)
    request.setUpsert(indexRequest)
    val version = request.execute.actionGet.getVersion
    if (version > 0) 1 else 0
  }

}
