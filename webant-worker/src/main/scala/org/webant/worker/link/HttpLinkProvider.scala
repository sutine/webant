package org.webant.worker.link

import org.apache.commons.collections.MapUtils
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.Link
import org.webant.commons.link.{ILinkProvider, Progress}
import org.webant.commons.utils.{HttpUtils, JsonUtils}
import org.webant.worker.config.ConfigManager

class HttpLinkProvider extends ILinkProvider {
  private val logger = LogManager.getLogger(classOf[HttpLinkProvider])
  private val host = ConfigManager.getWorkerConfig.node.queen.url
  private val SELECT_URL = s"$host/link/select"
  private val SAVE_URL = s"$host/link/save"
  private val SAVE_LIST_URL = s"$host/link/save/list"

  protected var taskId: String = _
  protected var siteId: String = _

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("url") || !params.containsKey("username")
      || !params.containsKey("password") || !params.containsKey("siteId"))
      return false

    val username = MapUtils.getString(params, "username")
    val password = MapUtils.getString(params, "password")
    batch = MapUtils.getInteger(params, "batch", 20)
    taskId = MapUtils.getString(params, "taskId")
    siteId = MapUtils.getString(params, "siteId")

    if (!StringUtils.isNotBlank(host)) {
      logger.error(s"init ${getClass.getSimpleName} failed!")
      return false
    }

    logger.info(s"init ${getClass.getSimpleName} success!")
    true
  }

  override def read(): Iterable[Link] = {
    HttpUtils.get(SELECT_URL)
//    val list = HttpUtils.get[util.ArrayList[Link]](SELECT_URL)
//    list.toArray(Array.empty[Link])
  }

  override def write(link: Link): Int = {
    if (link == null) return 0

    val json = JsonUtils.toJson(link)
    HttpUtils.post(SAVE_URL, json)
  }

  override def write(links: Iterable[Link]): Int = {
    if (links == null || links.isEmpty) return 0

    val json = JsonUtils.toJson(links.toArray)
    HttpUtils.post(SAVE_LIST_URL, json)
  }

  override def reset(status: String): Int = {
    0
  }

  override def progress(): Progress = {
    val t = total()

    val init = 0
    val pending = 0
    val success = 0
    val fail = 0

    Progress(t, init, pending, success, fail)
  }

  override def total(): Long = {
    0
  }

  override def count(status: String): Long = {
    0
  }

  override def close(): Boolean = {
    true
  }
}
