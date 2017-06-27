package org.webant.worker.console

import javax.management.{AttributeChangeNotification, NotificationBroadcasterSupport}

import org.apache.log4j.LogManager
import org.webant.worker.config.{SiteConfig, TaskConfig}
import org.webant.worker.link.Progress
import org.webant.worker.manager.WorkerManager

class ConsoleOperation extends NotificationBroadcasterSupport with ConsoleOperationMBean {
  private val logger = LogManager.getLogger(classOf[ConsoleOperation])

  val manager = new WorkerManager
  private var sequenceNo = 1

  override def list(): Iterable[Iterable[String]] = manager.list()

  def start(): Iterable[Iterable[String]] = {
    manager.start()

    //    run()
  }

  override def stop(): Iterable[Iterable[String]] = manager.stop()

  override def pause(): Iterable[Iterable[String]] = manager.pause()

  override def recrawl(): Iterable[Iterable[String]] = manager.recrawl()

  override def exit(): Iterable[Iterable[String]] = manager.exit()

  override def progress(): Progress = manager.progress()

  override def list(taskId: String): Iterable[String] = manager.list(taskId)

  override def start(taskId: String): Iterable[String] = manager.start(taskId)

  override def stop(taskId: String): Iterable[String] = manager.stop(taskId)

  override def pause(taskId: String): Iterable[String] = manager.pause(taskId)

  override def recrawl(taskId: String): Iterable[String] = manager.recrawl(taskId)

  override def progress(taskId: String): Progress = manager.progress(taskId)

  override def list(taskId: String, siteId: String): String = manager.list(taskId, siteId)

  override def start(taskId: String, siteId: String): String = manager.start(taskId, siteId)

  override def stop(taskId: String, siteId: String): String = manager.stop(taskId, siteId)

  override def pause(taskId: String, siteId: String): String = manager.pause(taskId, siteId)

  override def recrawl(taskId: String, siteId: String): String = manager.recrawl(taskId, siteId)

  override def progress(taskId: String, siteId: String): Progress = manager.progress(taskId, siteId)

  override def submit(taskConfig: TaskConfig): Unit = {
    if (manager != null)
      manager.submit(taskConfig)
  }

  override def submit(siteConfig: SiteConfig): Unit = {
    if (manager != null)
      manager.submit(siteConfig)
  }

  def submitTask(taskConfig: String): Unit = {
    if (manager != null)
      manager.submitTask(taskConfig)
  }
  def submitSite(siteConfig: String): Unit = {
    if (manager != null)
      manager.submitSite(siteConfig)
  }

  private def run(): Unit = {
    val interval = 1000
    val that = this

    // report progress
    new Thread() {
      override def run(): Unit = {
        while (true) {
          val p = manager.progress()
          val total = p.total
          val init = p.init
          val pending = p.pending
          val success = p.success
          val fail = p.fail

          val progress = s"crawl progress. total: $total. init: $init, pending: $pending, success: $success, fail: $fail."

          logger.info(progress)

          sequenceNo += 1
          val counts = Array(total, init, pending, success, fail)
          val notification = new AttributeChangeNotification(that, sequenceNo, System.currentTimeMillis, progress, "progress", "Long[]", "oldValue", counts)

          sendNotification(notification)

          Thread.sleep(interval)
        }
      }
    }.start()
  }


}
