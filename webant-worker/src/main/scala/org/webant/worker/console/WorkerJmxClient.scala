package org.webant.worker.console

import java.io.File
import java.util
import javax.management.remote.{JMXConnector, JMXConnectorFactory, JMXServiceURL}
import javax.management.{JMX, MBeanServerConnection, ObjectName}

import org.apache.commons.io.FileUtils
import org.webant.commons.utils.WebantConstants

object WorkerJmxClient {
  private var connector: JMXConnector = _
  private var mbeanProxy: ConsoleOperationMBean = _
  private var mbeanServer: MBeanServerConnection = _

  def connect(): String = {
    connect("localhost", "1099")
  }

  def connect(host: String, port: String): String = {
    try {
      val prop = new util.HashMap[String, AnyRef]
      prop.put(JMXConnector.CREDENTIALS, Array[String](WebantConstants.USERNAME, WebantConstants.PASSWORD))

      val jmxServerName = "WebantWorkerConsole"
      val jmxServiceUrl = s"service:jmx:rmi:///jndi/rmi://$host:$port/$jmxServerName"
      val jmxWorkerObjectName = s"$jmxServerName:name=WorkerJmxConsole"

      val url = new JMXServiceURL(jmxServiceUrl)
      connector = JMXConnectorFactory.connect(url, prop)
      mbeanServer = connector.getMBeanServerConnection

      val mbeanName = new ObjectName(jmxWorkerObjectName)
      mbeanServer.addNotificationListener(mbeanName, new WorkerNotificationListener, null, null)

      mbeanProxy = JMX.newMBeanProxy(mbeanServer, mbeanName, classOf[ConsoleOperationMBean], true)
    } catch {
      case e: Exception =>
        return s"connect to server $host:$port failed! error: ${e.getMessage}"
    }

    s"connect to server $host:$port success!"
  }

  def isConnected: Boolean = {
    mbeanProxy != null
  }

  def list(): Array[Array[String]] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.list().map(_.toArray).toArray
  }

  def list(taskId: String): Array[String] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.list(taskId).toArray
  }

  def list(taskId: String, siteId: String): String = {
    if (mbeanProxy == null)
      return "lost connection! please connect to server first."

    mbeanProxy.list(taskId, siteId)
  }

  def start(): Array[Array[String]] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.start().map(_.toArray).toArray
  }

  def start(taskId: String): Array[String] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.start(taskId).toArray
  }

  def start(taskId: String, siteId: String): String = {
    if (mbeanProxy == null)
      return "lost connection! please connect to server first."

    mbeanProxy.start(taskId, siteId)
  }

  def stop(): Array[Array[String]] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.stop().map(_.toArray).toArray
  }

  def stop(taskId: String): Array[String] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.stop(taskId).toArray
  }

  def stop(taskId: String, siteId: String): String = {
    if (mbeanProxy == null)
      return "lost connection! please connect to server first."

    mbeanProxy.stop(taskId, siteId)
  }

  def pause(): Array[Array[String]] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.pause().map(_.toArray).toArray
  }

  def pause(taskId: String): Array[String] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.pause(taskId).toArray
  }

  def pause(taskId: String, siteId: String): String = {
    if (mbeanProxy == null)
      return "lost connection! please connect to server first."

    mbeanProxy.pause(taskId, siteId)
  }

  def recrawl(): Array[Array[String]] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.recrawl().map(_.toArray).toArray
  }

  def recrawl(taskId: String): Array[String] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.recrawl(taskId).toArray
  }

  def recrawl(taskId: String, siteId: String): String = {
    if (mbeanProxy == null)
      return "lost connection! please connect to server first."

    mbeanProxy.recrawl(taskId, siteId)
  }

  def exit(): Array[Array[String]] = {
    if (mbeanProxy == null)
      return Array.empty

    mbeanProxy.exit().map(_.toArray).toArray
  }

  def progress(): String = {
    if (mbeanProxy == null)
      return "lost connection! please connect to server first."

    val progress = mbeanProxy.progress()
    if (progress.total == 0) {
      return "total progress 0%."
    }

    val gauge = f"${({progress.success} + {progress.fail}).toFloat * 100 / {progress.total}.toFloat}%2.2f"

    s"total progress $gauge%. total: ${progress.total}. init: ${progress.init}. " +
      s"pending: ${progress.pending}. success: ${progress.success}. fail: ${progress.fail}."
  }

  def progress(taskId: String): String = {
    if (mbeanProxy == null)
      return "lost connection! please connect to server first."

    val progress = mbeanProxy.progress(taskId)
    if (progress.total == 0) {
      return s"task($taskId) progress 0%."
    }

    val gauge = f"${({progress.success} + {progress.fail}).toFloat * 100 / {progress.total}.toFloat}%2.2f"

    s"task($taskId) progress $gauge%. total: ${progress.total}. init: ${progress.init}. " +
      s"pending: ${progress.pending}. success: ${progress.success}. fail: ${progress.fail}."
  }

  def progress(taskId: String, siteId: String): String = {
    if (mbeanProxy == null)
      return "lost connection! please connect to server first."

    val progress = mbeanProxy.progress(taskId, siteId)
    if (progress.total == 0) {
      return s"site($taskId, $siteId) progress 0%."
    }

    val gauge = f"${({progress.success} + {progress.fail}).toFloat * 100 / {progress.total}.toFloat}%2.2f"

    s"site($taskId, $siteId) progress $gauge%. total: ${progress.total}. init: ${progress.init}. " +
      s"pending: ${progress.pending}. success: ${progress.success}. fail: ${progress.fail}."
  }

  def submitTask(configPath: String): Boolean = {
    if (mbeanProxy == null)
      return false

    val file = new File(configPath)
    if (!file.exists() || !file.isFile) return false
    val content = FileUtils.readFileToString(file, "UTF-8")

    mbeanProxy.submitTask(content)
    true
  }

  def submitSite(configPath: String): Boolean = {
    if (mbeanProxy == null)
      return false

    val file = new File(configPath)
    if (!file.exists() || !file.isFile) return false
    val content = FileUtils.readFileToString(file, "UTF-8")

    mbeanProxy.submitSite(content)
    true
  }

  def shutdown(): String = {
    if (mbeanProxy == null || connector == null)
      return "lost connection! please connect to server first."

    try {
      mbeanProxy.exit()
      connector.close()
    } catch {
      case e: Exception =>
        return s"shutdown the webant worker server failed! error: ${e.getMessage}"
    }
    "shutdown the webant worker server success!"
  }
}
