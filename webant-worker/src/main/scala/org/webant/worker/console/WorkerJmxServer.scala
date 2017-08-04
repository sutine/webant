package org.webant.worker.console

import java.io.IOException
import java.rmi.registry.LocateRegistry
import java.util
import javax.management._
import javax.management.remote.{JMXAuthenticator, JMXConnectorServer, JMXConnectorServerFactory, JMXServiceURL}
import javax.security.auth.Subject

import org.apache.log4j.LogManager
import org.webant.worker.config.ConfigManager

object WorkerJmxServer {
  private val logger = LogManager.getLogger(WorkerJmxServer.getClass)
  private val JMX_SERVER_NAME = "WebantWorkerConsole"
  private val JMX_SERVER_HOST = ConfigManager.getWorkerConfig.server.serverHost
  private val JMX_SERVER_PORT = ConfigManager.getWorkerConfig.server.serverPort
  private val JMX_SERVICE_URL = s"service:jmx:rmi:///jndi/rmi://$JMX_SERVER_HOST:$JMX_SERVER_PORT/$JMX_SERVER_NAME"
  private val JMX_WORKER_OBJECT_NAME = s"$JMX_SERVER_NAME:name=WorkerJmxConsole"

  @throws[MalformedObjectNameException]
  @throws[NullPointerException]
  @throws[InstanceAlreadyExistsException]
  @throws[MBeanRegistrationException]
  @throws[NotCompliantMBeanException]
  @throws[IOException]
  def start() {
    val webant =
      """
        |****************************************************************
        | _          __  _____   _____       ___   __   _   _____
        || |        / / | ____| |  _  \     /   | |  \ | | |_   _|
        || |  __   / /  | |__   | |_| |    / /| | |   \| |   | |
        || | /  | / /   |  __|  |  _  {   / /_| | | |\   |   | |
        || |/   |/ /    | |___  | |_| |  / /__| | | | \  |   | |
        ||___/|___/     |_____| |_____/ /_/   |_| |_|  \_|   |_|
        |
        |
        |                        Version 1.0.0
        |
        |****************************************************************
      """.stripMargin
    logger.info(webant)

    LocateRegistry.createRegistry(JMX_SERVER_PORT)
    val mbeanServer = MBeanServerFactory.createMBeanServer
    val prop = new util.HashMap[String, AnyRef]
    prop.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator() {
      override def authenticate(credentials: scala.Any): Subject = {
        credentials match {
          case account: Array[String] =>
            if (account(0) == ConfigManager.getWorkerConfig.server.username
              && account(1) == ConfigManager.getWorkerConfig.server.password)
              return new Subject
          case _ =>
        }
        throw new SecurityException("authentication failed!")
      }
    })

    val objName = new ObjectName(JMX_WORKER_OBJECT_NAME)
    mbeanServer.registerMBean(new ConsoleOperation, objName)

    val url = new JMXServiceURL(JMX_SERVICE_URL)
    logger.info("start console success. JMXServiceURL: " + url.toString)
    val jmxConnServer = JMXConnectorServerFactory.newJMXConnectorServer(url, prop, mbeanServer)
    jmxConnServer.start()
  }
}
