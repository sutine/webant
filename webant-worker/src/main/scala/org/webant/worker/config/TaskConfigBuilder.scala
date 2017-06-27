package org.webant.worker.config

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.commons.utils.JsonUtils

class TaskConfigBuilder {
  private val logger = LogManager.getLogger(classOf[TaskConfigBuilder])

  private var taskConfig = new TaskConfig

  def loadTaskConfig(path: String): TaskConfigBuilder = {
    require(StringUtils.isNotBlank(path), "task config path can not be empty.")
    val file = new File(path)
    require(file.exists(), "task config does not exists.")
    require(!file.isDirectory, "task config can not be a directory.")
    val content = FileUtils.readFileToString(file, "UTF-8")

    if (StringUtils.isNotBlank(content)) {
      taskConfig = JsonUtils.fromJson(content, classOf[TaskConfig])
      logger.info(s"loading task config ${taskConfig.id}(${taskConfig.name}) from ${file.getAbsolutePath}")
    }

    this
  }

  def build(): TaskConfig = {
    require(StringUtils.isNotBlank(taskConfig.id), "id can not be empty!")
    require(taskConfig.sites != null && taskConfig.sites.nonEmpty, "sites can not be empty!")

    taskConfig
  }

  def id(id: String): TaskConfigBuilder = {
    taskConfig.id = id
    this
  }

  def name(name: String): TaskConfigBuilder = {
    taskConfig.name = name
    this
  }

  def description(description: String): TaskConfigBuilder = {
    taskConfig.description = description
    this
  }

  def priority(priority: Integer): TaskConfigBuilder = {
    taskConfig.priority = priority
    this
  }

  def sites(sites: Array[String]): TaskConfigBuilder = {
    taskConfig.sites = sites
    this
  }
}




