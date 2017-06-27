package org.webant.worker.http

import scala.collection.mutable

/**
  * Created by Administrator on 2016/11/19.
  */
object ProxyManager {
  private final val siteCrawlTime = mutable.Map.empty[String, Long]

  def getLastCrawlTime(site: String, ip: String): Long = {
    val key = s"${site}_$ip"
    if (siteCrawlTime.contains(key))
      siteCrawlTime(key)
    else
      0L
  }

  def setLastCrawlTime(site: String, ip: String, time: Long): Unit = {
    val key = s"${site}_$ip"
    siteCrawlTime(key) = time
  }
}
