package org.webant.worker.manager

import org.webant.worker.config.ConfigManager
import org.webant.worker.link.Progress

class TaskManager(taskId: String) {
  private var isRunning = false

  private val siteManagers = getSites

  def getSites: Map[String, SiteManager] = {
    ConfigManager.getTaskConfig(taskId).sites.map(siteId => (siteId, new SiteManager(taskId, siteId))).toMap
  }

  def getSiteManager(siteId: String): SiteManager = {
    if (siteManagers == null || siteManagers.isEmpty)
      return null

    siteManagers(siteId)
  }

  def list(): Iterable[String] = {
    ConfigManager.getTaskConfig(taskId).sites.map(list)
  }

  def start(): Iterable[String] = {
    val results = ConfigManager.getTaskConfig(taskId).sites.map(start)
    isRunning = true

    results
  }

  def pause(): Iterable[String] = {
    val results = ConfigManager.getTaskConfig(taskId).sites.map(pause)
    isRunning = false

    results
  }

  def stop(): Iterable[String] = {
    val results = ConfigManager.getTaskConfig(taskId).sites.map(stop)
    isRunning = false

    results
  }

  def recrawl(): Iterable[String] = {
    val results = ConfigManager.getTaskConfig(taskId).sites.map(recrawl)
    isRunning = true

    results
  }

  def exit(): Iterable[String] = {
    val results = ConfigManager.getTaskConfig(taskId).sites.map(exit)
    isRunning = true

    results
  }

  def progress(): Progress = {
    siteManagers.values
      .map(_.progress())
      .reduce((left, right) => Progress(left.total + right.total, left.init + right.init, left.pending + right.pending, left.success + right.success, left.fail + right.fail))
  }

  def list(siteId: String): String = {
    val site = siteManagers.get(siteId).orNull
    site.list()
  }

  def start(siteId: String): String = {
    val site = siteManagers.get(siteId).orNull
    var info = ""
    try {
      if (site != null) {
        info = site.start()

        isRunning = true
      } else
        info = s"the site $siteId is not exists!"
    } catch {
      case e: Exception =>
        return s"start site $taskId($siteId) failed! error: ${e.getMessage}"
    }

    info
  }

  def pause(siteId: String): String = {
    val site = siteManagers.get(siteId).orNull
    var info = ""
    try {
      if (site != null) {
        info = site.pause()
        isRunning = false
      } else
        info = s"the site $siteId is not exists!"
    } catch {
      case e: Exception =>
        return s"pause site $siteId failed! error: ${e.getMessage}"
    }

    info
  }

  def stop(siteId: String): String = {
    val site = siteManagers.get(siteId).orNull
    var info = ""
    try {
      if (site != null) {
        info = site.stop()
        isRunning = false
      } else
        info = s"the site $siteId is not exists!"
    } catch {
      case e: Exception =>
        return s"stop site $siteId failed! error: ${e.getMessage}"
    }

    info
  }

  def recrawl(siteId: String): String = {
    val site = siteManagers.get(siteId).orNull
    var info = ""
    try {
      if (site != null) {
        info = site.recrawl()
        isRunning = true
      } else
        info = s"the site $siteId is not exists!"
    } catch {
      case e: Exception =>
        return s"reset site $siteId to recrawl failed! error: ${e.getMessage}"
    }

    info
  }

  def exit(siteId: String): String = {
    val site = siteManagers.get(siteId).orNull
    var info = ""
    try {
      if (site != null) {
        info = site.exit()
      } else
        info = s"the site $siteId is not exists!"
    } catch {
      case e: Exception =>
        return s"exit site $siteId failed! error: ${e.getMessage}"
    }

    info
  }

  def progress(siteId: String): Progress = {
    val site = siteManagers.get(siteId).orNull
    if (site != null)
      site.progress()
    else
      Progress(0, 0, 0, 0, 0)
  }
}
