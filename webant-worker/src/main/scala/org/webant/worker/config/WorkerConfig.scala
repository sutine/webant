package org.webant.worker.config

import java.io.File
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.{JAXBContext, JAXBException}

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager

import scala.beans.BeanProperty

@XmlRootElement
class WorkerConfig extends java.io.Serializable {
  @BeanProperty
  var id: String = _
  @BeanProperty
  var name: String = _
  @BeanProperty
  var description: String = _
  @BeanProperty
  var mode: String = WorkerConfig.WORKER_RUN_MODE_STANDALONE
  @BeanProperty
  var threadNum: Int = 32
  @BeanProperty
  var dataDir: String = "./data"
  @BeanProperty
  var standalone: Standalone = _
  @BeanProperty
  var server: Server = _
  @BeanProperty
  var node: Node = _
}

class Standalone extends java.io.Serializable {
  @BeanProperty
  var taskMonitor: ConfigMonitor = _
  @BeanProperty
  var siteMonitor: ConfigMonitor = _
}

class Server extends java.io.Serializable {
  @BeanProperty
  var serverHost: String = "localhost"
  @BeanProperty
  var serverPort: Int = 1099
  @BeanProperty
  var username: String = "webant"
  @BeanProperty
  var password: String = "webant"
}

class Node extends java.io.Serializable {
  @BeanProperty
  var queen: Queen = _
}

class ConfigMonitor extends java.io.Serializable {
  @BeanProperty
  var dir: String = _
  @BeanProperty
  var interval: Int = _
}

class Queen extends java.io.Serializable {
  @BeanProperty
  var url: String = "http://localhost"
}

object WorkerConfig {
  private val logger = LogManager.getLogger(classOf[WorkerConfig])
  val WORKER_VERSION = "1.0.0"
  val WORKER_RUN_MODE_STANDALONE = "standalone"
  val WORKER_RUN_MODE_SERVER = "server"
  val WORKER_RUN_MODE_NODE = "node"

  def apply(path: String): WorkerConfig = {
    require(StringUtils.isNotBlank(path), "worker config path can not be empty.")
    val configPath = ClassLoader.getSystemResource(path)
    val file = new File(configPath.getPath)
    require(file.exists(), "worker config does not exists.")
    require(file.isFile, "worker config can not be a directory.")

    var config: WorkerConfig = null

    try {
      val context = JAXBContext.newInstance(classOf[WorkerConfig])
      val unmarshaller = context.createUnmarshaller
      config = unmarshaller.unmarshal(file).asInstanceOf[WorkerConfig]
    } catch {
      case e: JAXBException =>
        logger.error(s"parse worker config failed! error: ${e.getMessage}")
    }

    require(config != null, "parse worker config failed!")
    require(config.mode == WORKER_RUN_MODE_STANDALONE || config.mode == WORKER_RUN_MODE_SERVER
      || config.mode == WORKER_RUN_MODE_NODE, "worker mode must be standalone or server or node!")

    config
  }
}