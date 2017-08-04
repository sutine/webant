package org.webant.worker.config

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.{SiteConfig, TaskConfig, TaskEntity}
import org.webant.commons.utils.HttpUtils
import org.webant.worker.manager.TaskManager

import scala.collection.mutable

object ConfigManager {
  private val logger = LogManager.getLogger(ConfigManager.getClass)

  private var workerConfig: WorkerConfig = new WorkerConfig

  private val siteConfigs = mutable.Map.empty[String, SiteConfig]
  private val taskConfigs = mutable.Map.empty[String, TaskConfig]

//  private val sites = mutable.Map.empty[String, SiteManager]
  private val tasks = mutable.Map.empty[String, TaskManager]

  def submit(workerConfig: WorkerConfig): Unit = {
    this.workerConfig = workerConfig
    logger.info(s"submit worker config success!")
  }

  def submit(siteConfig: SiteConfig): Unit = {
    require(StringUtils.isNotBlank(siteConfig.id), "id can not be empty!")
    require(siteConfig.seeds != null && siteConfig.seeds.nonEmpty, "seeds can not be empty!")
    require(siteConfig.processors != null && siteConfig.processors.nonEmpty, "processors can not be empty!")

    siteConfigs.put(siteConfig.id, siteConfig)
//    sites.put(siteConfig.id, new SiteManager(siteConfig.id))
    logger.info(s"submit site ${siteConfig.id}(${siteConfig.name}) success!")
  }

  def submit(taskConfig: TaskConfig): Unit = {
    require(StringUtils.isNotBlank(taskConfig.getId), "task id can not be empty!")
    require(taskConfig.getSites != null && taskConfig.getSites.nonEmpty, "sites can not be empty!")

    taskConfigs.put(taskConfig.getId, taskConfig)
    tasks.put(taskConfig.getId, new TaskManager(taskConfig.getId))
    logger.info(s"submit task ${taskConfig.getId}(${taskConfig.getName}) success!")
  }

  def loadContent(path: String): String = {
    val configPath = ClassLoader.getSystemResource(path)
    if (configPath == null) return null

    val file = new File(configPath.getPath)
    if (!file.exists() || !file.isFile) return null

    FileUtils.readFileToString(file, "UTF-8")
  }

  def loadWorkerConfig(path: String): Unit = {
    workerConfig = WorkerConfig(path)
  }

  /*
    def loadTaskConfig(path: String): TaskConfig = {
      require(StringUtils.isNotBlank(path), "task config path can not be empty.")
      val file = new File(path)
      require(file.exists(), "task config does not exists.")
      require(file.isFile, "task config can not be a directory.")

      val content = FileUtils.readFileToString(file, "UTF-8")
      if (StringUtils.isNotBlank(content)) {
        val taskConfig = JsonUtils.fromJson(content, classOf[TaskConfig])
        logger.info(s"init task ${taskConfig.id}(${taskConfig.name}) success!")
        taskConfig
      } else null
    }

    def loadSiteConfig(path: String): SiteConfig = {
      require(StringUtils.isNotBlank(path), "site config path can not be empty.")
      val file = new File(path)
      require(file.exists(), "site config does not exists.")
      require(file.isFile, "site config can not be a directory.")

      val content = FileUtils.readFileToString(file, "UTF-8")
      if (StringUtils.isNotBlank(content)) {
        val siteConfig = JsonUtils.fromJson(content, classOf[SiteConfig])

        siteConfig.processors.foreach(StoreFactory.load)
        logger.info(s"init site ${siteConfig.id}(${siteConfig.name}) success!")
        siteConfig
      } else null
    }
  */

  def getWorkerConfig: WorkerConfig = workerConfig

  def getTaskConfig(taskId: String): TaskConfig = {
    require(taskConfigs.contains(taskId), s"the task config $taskId has not found!")

    taskConfigs(taskId)
  }

  def getSiteConfig(siteId: String): SiteConfig = {
    require(siteConfigs.contains(siteId), s"the site config $siteId has not found!")

    siteConfigs(siteId)
  }

  def getTaskManager(taskId: String): TaskManager = {
    require(tasks.contains(taskId), s"the task manager $taskId has not found!")
    if (!tasks.contains(taskId)) {
      val taskConfig = HttpUtils.getTaskConfig(taskId)
      if (taskConfig != null) {
        val task = new TaskEntity(taskConfig)
        val sites = taskConfig.getSites
        tasks += (taskConfig.getId -> new TaskManager(task.getId))
//        sites.foreach(site => siteConfigs.put(site.id, site))
      }
    }

    tasks(taskId)
  }

/*
  def getSiteManager(siteId: String): SiteManager = {
    require(sites.contains(siteId), s"the site manager $siteId has not found!")

    sites(siteId)
  }
*/

  def getTasks: mutable.Map[String, TaskManager] = tasks

  def getTaskConfigs: mutable.Map[String, TaskConfig] = taskConfigs

/*
  def getPriorityTasks: Iterable[TaskManager] = tasks.values.toSeq.sortBy(-_.priority)

  def getPriorityTasksMgr: Iterable[TaskManager] = tasks.values.toSeq.sortBy(-_.priority)
*/
}
