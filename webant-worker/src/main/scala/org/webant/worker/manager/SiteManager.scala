package org.webant.worker.manager

import java.util.Date

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.Link
import org.webant.commons.link.{ILinkProvider, Progress}
import org.webant.worker.WorkerReactor
import org.webant.worker.config.{ConfigManager, WorkerConfig}
import org.webant.worker.link.{H2LinkProvider, HttpLinkProvider}
import org.webant.worker.processor.HttpSiteProcessor
import org.webant.worker.store.StoreFactory

class SiteManager(taskId: String, siteId: String) {
  private val logger = LogManager.getLogger(classOf[SiteManager])

  var isStopped = false
  var isPaused = false
  var isRunning = false

  private val linkProvider: ILinkProvider = getLinkProvider
  ConfigManager.getSiteConfig(taskId, siteId).processors.foreach(StoreFactory.load)

  def list(): String = {
    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    val status = if (isRunning) "running" else if (isStopped) "stopped" else if (isPaused) "paused" else "inited"
    s"${siteConfig.id}  ${siteConfig.name}  ${siteConfig.description} $status"
  }

  def start(): String = {
    if (isStopped)
      isStopped = !isStopped

    if (isPaused)
      isPaused = !isPaused

    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    var info = ""

    if (isRunning) {
      info = s"the site ${siteConfig.name}(${siteConfig.id}) has already started!"
      logger.info(info)
      return info
    }

    try {
      submitSeeds()
      run()
      isRunning = true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        info = s"start site ${siteConfig.name}(${siteConfig.id}) failed! error: ${e.getMessage}"
        logger.error(info)
        return info
    }

    info = s"start site ${siteConfig.name}(${siteConfig.id}) success."
    logger.info(info)
    info
  }

  def pause(): String = {
    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    var info = ""
    if (!isRunning) {
      info = s"the site ${siteConfig.name}(${siteConfig.id}) is not running!"
      logger.info(info)
      return info
    }

    isPaused = !isPaused
    if (isPaused) {
      isRunning = false
      info = s"pause site ${siteConfig.name}(${siteConfig.id}) success."
      logger.info(info)
    } else {
      isRunning = true
      info = s"resume site ${siteConfig.name}(${siteConfig.id}) success."
      logger.info(info)
    }

    info
  }

  def stop(): String = {
    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    var info = ""
    if (isStopped) {
      info = s"the site ${siteConfig.name}(${siteConfig.id}) has already stopped!"
      logger.info(info)
      return info
    }

    isStopped = true
    isRunning = false
    isPaused = false
    info = s"stop site ${siteConfig.name}(${siteConfig.id}) success."
    logger.info(info)

    info
  }

  def isComplete: Boolean = {
    val p = progress()
    p.init == 0 && p.pending == 0 && p.total == p.success + p.fail
  }

  def progress(): Progress = {
    require(linkProvider != null)
    linkProvider.progress()
  }

  def reset(): String = {
    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    var info = ""
    try {
      linkProvider.reset(Link.LINK_STATUS_PENDING)
      linkProvider.reset(Link.LINK_STATUS_SUCCESS)
      linkProvider.reset(Link.LINK_STATUS_FAIL)
      info = s"reset site ${siteConfig.name}(${siteConfig.id}) success."
      logger.info(info)
    } catch {
      case e: Exception =>
        info = s"reset site ${siteConfig.name}(${siteConfig.id}) failed! error: ${e.getMessage}"
        logger.error(info)
        return info
    }

    info
  }

  def exit(): String = {
    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    var info = ""
    try {
      stop()
      linkProvider.close()
      info = s"exit site ${siteConfig.name}(${siteConfig.id}) success."
      logger.info(info)
    } catch {
      case e: Exception =>
        info = s"exit site ${siteConfig.name}(${siteConfig.id}) failed! error: ${e.getMessage}"
        logger.error(info)
        return info
    }
    info
  }

  def getSiteProcessor: HttpSiteProcessor = {
    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    new HttpSiteProcessor(linkProvider, siteConfig)
  }

  private def getLinkProvider: ILinkProvider = {
    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    var provider: ILinkProvider = null
    if (WorkerConfig.WORKER_RUN_MODE_NODE == ConfigManager.getWorkerConfig.getMode) {
      provider = new HttpLinkProvider
      return provider
    }
    if (siteConfig.linkProvider != null && StringUtils.isNotBlank(siteConfig.linkProvider.getClassName)) {
      try {
        provider = Class.forName(siteConfig.linkProvider.getClassName).newInstance().asInstanceOf[ILinkProvider]
        siteConfig.linkProvider.getParams.put("taskId", taskId)
        siteConfig.linkProvider.getParams.put("siteId", siteConfig.id)
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }

    if(provider == null || !provider.init(siteConfig.linkProvider.getParams)) {
      logger.error("init link provider failed! use default H2LinkProvider.")
      val h2Provider = new H2LinkProvider()
      h2Provider.init(taskId, siteConfig.id)

      provider = h2Provider
    }

    provider
  }

  private def submitSeeds(): Unit = {
    val siteConfig = ConfigManager.getSiteConfig(taskId, siteId)
    val seeds = siteConfig.seeds.map(seed => new Link(DigestUtils.md5Hex(seed), taskId, siteConfig.id, seed, null, new Date()))
    WorkerReactor.submit(seeds)
  }

  // select links to process
  private def run(): Unit = {
    val intervalUnit = 100
    val intervalMax = 1000
    var interval = intervalUnit
    var noopCount = 0

    var lastPendingCount = 0l

    var lastDeltaTime = System.currentTimeMillis()

    new Thread(s"[$siteId], site manager thread") {
      override def run(): Unit = {
        while (!isStopped) {
          try {
            val site = ConfigManager.getSiteConfig(taskId, siteId)

            if (!isPaused) {
              val links = linkProvider.read()
              if (links.nonEmpty) {
                if (site.timeInterval > 0) {
                  links.foreach(link => {
                    WorkerReactor.submit(link)
                    Thread.sleep(site.timeInterval)
                  })
                } else {
                  WorkerReactor.submit(links)
                }

                interval = intervalUnit
                noopCount = 0
              } else {
                noopCount += 1
                // reset pending links
                val pendingCount = linkProvider.count(Link.LINK_STATUS_PENDING)
                if (pendingCount != lastPendingCount)
                  lastPendingCount = pendingCount
                else if (pendingCount != 0)
                  linkProvider.reset(Link.LINK_STATUS_PENDING)

                // reset banned links
              }

              if (noopCount == 10 && interval < intervalMax) {
                noopCount = 0
                interval += intervalUnit
              }
            }

            // submit seeds to delta crawl
            val now = System.currentTimeMillis()
            if (site.incrementInterval > 0 && now - lastDeltaTime >= site.incrementInterval) {
              submitSeeds()
              lastDeltaTime = now
            }
          } catch {
            case e: Exception =>
              logger.error(s"anything error! ${e.getMessage}")
          }
/*
          if (isComplete()) {
            logger.info(s"crawl site ${siteConfig.id} complete!")
            isStopped = true
          }
*/
          Thread.sleep(interval)
        }
      }
    }.start()
  }
}
