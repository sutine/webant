package org.webant.commons.utils

import java.nio.charset.Charset

import com.google.gson.reflect.TypeToken
import org.apache.commons.lang3.StringUtils
import org.apache.http.entity.ContentType
import org.webant.commons.entity.{Link, TaskConfig, TaskEntity}
import org.webant.commons.http.HttpDataResponse

import scala.reflect.ClassTag

object HttpUtils {
  private val UA = "Webant worker http client"
/*
  def get[T : ClassTag](url: String): T = {
    val resp = org.apache.http.client.fluent.Request.Get(url)
      .addHeader("User-Agent", UA)
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    if (StringUtils.isBlank(result))
      return null.asInstanceOf[T]

    val response = SJsonUtils.fromJson[HttpDataResponse[T]](result, new TypeToken[HttpDataResponse[T]] {}.getType)
    if (response.isSuccess)
      response.getData
    else null.asInstanceOf[T]
  }

  def post[T : ClassTag](url: String, json: String): T = {
    val resp = org.apache.http.client.fluent.Request.Post(url)
      .bodyString(json, ContentType.APPLICATION_JSON)
      .addHeader("User-Agent", UA)
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    if (StringUtils.isBlank(result))
      return null.asInstanceOf[T]

    val response = JsonUtils.fromJson[HttpDataResponse[T]](result, new TypeToken[HttpDataResponse[T]] {}.getType)
    response.getData
  }
*/

  def get(url: String): Array[Link] = {
    val resp = org.apache.http.client.fluent.Request.Get(url)
      .addHeader("User-Agent", UA)
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    if (StringUtils.isBlank(result))
      return Array.empty

    val response = JsonUtils.fromJson[HttpDataResponse[Array[Link]]](result, new TypeToken[HttpDataResponse[Array[Link]]] {}.getType)
    response.getData
  }

  def post(url: String, json: String): Int = {
    val resp = org.apache.http.client.fluent.Request.Post(url)
      .bodyString(json, ContentType.APPLICATION_JSON)
      .addHeader("User-Agent", UA)
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    if (StringUtils.isBlank(result))
      return 0

    val response = JsonUtils.fromJson[HttpDataResponse[Integer]](result, new TypeToken[HttpDataResponse[Integer]] {}.getType)
    response.getData
  }

  def getTaskConfig(taskId: String): TaskConfig = {
    val host = "http://localhost:8080/"

    val url = s"$host/site/start?siteId=$taskId"

    val resp = org.apache.http.client.fluent.Request.Get(url)
      .addHeader("User-Agent", UA)
      .execute
    val result = resp.returnContent.asString(Charset.forName("UTF-8"))
    if (StringUtils.isBlank(result))
      return null

    val response = JsonUtils.fromJson[HttpDataResponse[TaskConfig]](result, new TypeToken[HttpDataResponse[Array[TaskConfig]]] {}.getType)
    response.getData
  }

}
