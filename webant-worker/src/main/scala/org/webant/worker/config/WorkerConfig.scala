package org.webant.worker.config

import java.io.File

import org.apache.commons.lang3.StringUtils

import scala.xml.XML

class WorkerConfig extends java.io.Serializable {
  var id: String = _
  var name: String = _
  var description: String = _
  var serverHost: String = "localhost"
  var serverPort: Int = 1099
  var threadNum: Int = 32
  var dataDir: String = "./data"
  var queen: Queen = _
  var taskMonitor: ConfigMonitor = _
  var siteMonitor: ConfigMonitor = _
}

class Queen extends java.io.Serializable {
  var url: String = "http://localhost"
}

class ConfigMonitor extends java.io.Serializable {
  var dir: String = _
  var interval: Int = _
}

object WorkerConfig {
  def apply(path: String): WorkerConfig = {
    require(StringUtils.isNotBlank(path), "config file path can not be empty.")
    val configPath = ClassLoader.getSystemResource(path)
    require(StringUtils.isNotBlank(path), "worker config path can not be empty.")
    val file = new File(configPath.getPath)
    require(file.exists(), "worker config does not exists.")
    require(file.isFile, "worker config can not be a directory.")
    val xml = XML.loadFile(file)

    val config = new WorkerConfig()

    config.id = xml\\"worker"\\"id"text

    config.name = xml\\"name"\\"id"text

    config
  }
}