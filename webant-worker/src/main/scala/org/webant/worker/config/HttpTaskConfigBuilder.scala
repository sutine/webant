package org.webant.worker.config

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.{SiteConfig, TaskConfig}
import org.webant.commons.utils.{HttpUtils, JsonUtils}

class HttpTaskConfigBuilder {
  private val logger = LogManager.getLogger(classOf[HttpTaskConfigBuilder])

  private var taskConfig = new TaskConfig

  def loadTaskConfig(url: String): HttpTaskConfigBuilder = {
    require(StringUtils.isNotBlank(url), "task config url can not be empty.")

    taskConfig = HttpUtils.getTaskConfig(url)
    logger.info(s"loading http task config ${taskConfig.getId}(${taskConfig.getName}) from $url")

    this
  }

  def build(): TaskConfig = {
    require(StringUtils.isNotBlank(taskConfig.getId), "id can not be empty!")
    require(taskConfig.getSites != null && taskConfig.getSites.nonEmpty, "sites can not be empty!")

    taskConfig
  }

  def id(id: String): HttpTaskConfigBuilder = {
    taskConfig.setId(id)
    this
  }

  def name(name: String): HttpTaskConfigBuilder = {
    taskConfig.setName(name)
    this
  }

  def description(description: String): HttpTaskConfigBuilder = {
    taskConfig.setDescription(description)
    this
  }

  def priority(priority: Integer): HttpTaskConfigBuilder = {
    taskConfig.setPriority(priority)
    this
  }

  def sites(sites: Array[SiteConfig]): HttpTaskConfigBuilder = {
    taskConfig.setSites(sites)
    this
  }
}




