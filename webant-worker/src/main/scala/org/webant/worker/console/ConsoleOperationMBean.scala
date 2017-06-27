package org.webant.worker.console

import org.webant.worker.config.{SiteConfig, TaskConfig}
import org.webant.worker.link.Progress

trait ConsoleOperationMBean {
  def list(): Iterable[Iterable[String]]
  def start(): Iterable[Iterable[String]]
  def pause(): Iterable[Iterable[String]]
  def stop(): Iterable[Iterable[String]]
  def recrawl(): Iterable[Iterable[String]]
  def exit(): Iterable[Iterable[String]]
  def progress(): Progress

  def list(taskId: String): Iterable[String]
  def start(taskId: String): Iterable[String]
  def pause(taskId: String): Iterable[String]
  def stop(taskId: String): Iterable[String]
  def recrawl(taskId: String): Iterable[String]
  def progress(taskId: String): Progress

  def list(taskId: String, siteId: String): String
  def start(taskId: String, siteId: String): String
  def pause(taskId: String, siteId: String): String
  def stop(taskId: String, siteId: String): String
  def recrawl(taskId: String, siteId: String): String
  def progress(taskId: String, siteId: String): Progress

  def submit(taskConfig: TaskConfig)
  def submit(siteConfig: SiteConfig)

  def submitTask(taskConfig: String)
  def submitSite(siteConfig: String)
}
