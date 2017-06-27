package org.webant.worker

import java.io.File
import java.net.{MalformedURLException, URL, URLClassLoader}
import java.util.jar.JarFile

import org.apache.commons.lang3.StringUtils
import org.apache.http.protocol.HttpProcessor
import org.apache.log4j.LogManager
import org.webant.worker.annotation.ProcessorName

import scala.collection.JavaConverters._

object WorkerApp {
  private val logger = LogManager.getLogger(WorkerApp.getClass)

  def main(args: Array[String]): Unit = {
/*
    new HttpSiteProcessorBuilder()
      .id("id1").name("name1").description("desc1").interval(1000).seeds(Array("http://www.mahua.com"))
        .create()
*/
  }

  def start(): Unit = {
    val jarPath = "site/jars"
    val interval = 500
    if (StringUtils.isBlank(jarPath)) return

    try {
      val file = new File(jarPath)
      if (!file.exists() || file.isDirectory) return

      val url:URL = file.toURI.toURL
      val urls = Array(url)
      val urlClassLoader = new URLClassLoader(urls)
      val processorClasses = getAllProcessors(urlClassLoader, jarPath)
      if (processorClasses == null || processorClasses.isEmpty) return

/*
      processorClasses.values.foreach(clazz => {
        val worker = clazz.asSubclass(classOf[HttpProcessor[_, _, _]]).newInstance()
        worker.start(interval)
      })
*/

    } catch {
      case e: MalformedURLException =>
        logger.error(s"invalid jar url, path: $jarPath")
    }
  }

  def getAllProcessors(classLoader: URLClassLoader, jarPath: String): Map[String, Class[_]] = {
    var processors = Map.empty[String, Class[_]]
    if (StringUtils.isBlank(jarPath) || classLoader == null) return processors

    try {
      val jarFile = new JarFile(jarPath)
      jarFile.entries().asScala.foreach(entry => {
        val path = entry.getName
        if (path != null && path.contains("/") && path.contains(".class")) {
          val classPath = path.replace("/", ".").substring(0, path.length() - 6)
          val clazz = classLoader.loadClass(classPath)
          if (clazz.isAnnotationPresent(classOf[ProcessorName])) {
            val annotation = clazz.getAnnotation(classOf[ProcessorName])
            if (StringUtils.isBlank(annotation.urlPattern()))
              logger.error(s"empty urlPattern, class: ${annotation.annotationType().getName}")

            processors += (annotation.urlPattern() -> clazz)
          }
        }
      })
    } catch {
      case e: Exception =>
        logger.error(s"load site jar file failed! jar file: $jarPath")
    }
    processors
  }
}
