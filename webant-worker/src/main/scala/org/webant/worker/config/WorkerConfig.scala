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
  var serverHost: String = "localhost"
  @BeanProperty
  var serverPort: Int = 1099
  @BeanProperty
  var threadNum: Int = 32
  @BeanProperty
  var dataDir: String = "./data"
  @BeanProperty
  var queen: Queen = _
  @BeanProperty
  var taskMonitor: ConfigMonitor = _
  @BeanProperty
  var siteMonitor: ConfigMonitor = _
}

class Queen extends java.io.Serializable {
  @BeanProperty
  var url: String = "http://localhost"
}

class ConfigMonitor extends java.io.Serializable {
  @BeanProperty
  var dir: String = _
  @BeanProperty
  var interval: Int = _
}

object WorkerConfig {
  private val logger = LogManager.getLogger(classOf[WorkerConfig])

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

    config
  }
}