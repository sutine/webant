package org.webant.worker.store

import java.nio.charset.Charset

import com.google.gson.reflect.TypeToken
import org.apache.commons.collections.MapUtils
import org.apache.commons.lang3.StringUtils
import org.apache.http.entity.ContentType
import org.apache.log4j.LogManager
import org.webant.commons.entity.HttpDataEntity
import org.webant.commons.http.HttpDataResponse
import org.webant.commons.store.IStore
import org.webant.commons.utils.JsonUtils

class HttpStore[T <: HttpDataEntity] extends IStore[T] {
  private val logger = LogManager.getLogger(classOf[HttpStore[HttpDataEntity]])
  private var url: String = _
  private var username: String = _
  private var password: String = _

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("url") || !params.containsKey("username") || !params.containsKey("password"))
      return false

    url = MapUtils.getString(params, "url")
    username = MapUtils.getString(params, "username")
    password = MapUtils.getString(params, "password")

    StringUtils.isNotBlank(url)
  }

  override def upsert(list: Iterable[T]): Int = {
    if (list == null || list.isEmpty) return 0

    var affectRowCount = 0

    try {
      val json = JsonUtils.toJson(list)
      affectRowCount = post(url, json)
    } catch {
      case e: Exception =>
        logger.error(e)
    }

    affectRowCount
  }

  override def upsert(data: T): Int = {
    if (data == null) return 0

    var affectRowCount = 0

    try {
      val json = JsonUtils.toJson(data)
      affectRowCount = post(url, json)
    } catch {
      case e: Exception =>
        logger.error(e)
    }

    affectRowCount
  }

  private def post(url: String, json: String): Int = {
    val resp = org.apache.http.client.fluent.Request.Post(url)
      .bodyString(json, ContentType.APPLICATION_JSON)
      .addHeader("Accept", "text/html,application/json,application/xml;")
      .addHeader("User-Agent", "Webant worker http store")
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    if (StringUtils.isBlank(result))
      return 0

    val response = JsonUtils.fromJson[HttpDataResponse[Integer]](result, new TypeToken[HttpDataResponse[Integer]] {}.getType)
    response.getData
  }
}