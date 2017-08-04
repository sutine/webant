package org.webant.worker.manager

import org.webant.commons.entity.{SiteConfig, TaskConfig}
import org.webant.commons.link.Progress
import org.webant.commons.utils.JsonUtils
import org.webant.worker.WorkerReactor
import org.webant.worker.config.ConfigManager

class WorkerManager {
  var isRunning = false
  WorkerReactor.start()

  def list(): Iterable[Iterable[String]] = {
    ConfigManager.getTaskConfigs.values.map(taskConfig => list(taskConfig.getId))
  }

  def start(): Iterable[Iterable[String]] = {
    var result = Iterable.empty[Iterable[String]]
    if (isRunning) return result

    try {
      result = ConfigManager.getTaskConfigs.values.map(taskConfig => start(taskConfig.getId))

      isRunning = true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def pause(): Iterable[Iterable[String]] = {
    var result = Iterable.empty[Iterable[String]]

    try {
      result = ConfigManager.getTaskConfigs.values.map(taskConfig => pause(taskConfig.getId))

      isRunning = false
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def stop(): Iterable[Iterable[String]] = {
    var result = Iterable.empty[Iterable[String]]

    try {
      result = ConfigManager.getTaskConfigs.values.map(taskConfig => stop(taskConfig.getId))

      isRunning = false
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def reset(): Iterable[Iterable[String]] = {
    var result = Iterable.empty[Iterable[String]]

    try {
      result = ConfigManager.getTaskConfigs.values.map(taskConfig => reset(taskConfig.getId))

      isRunning = true
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def exit(): Iterable[Iterable[String]] = {
    var result = Iterable.empty[Iterable[String]]

    try {
      result = ConfigManager.getTaskConfigs.values.map(taskConfig => exit(taskConfig.getId))
      WorkerReactor.exit()
      new Thread() {
        override def run(): Unit = {
          Thread.sleep(100)
          System.exit(0)
        }
      }.start()
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def progress(): Progress = {
    val tasks = ConfigManager.getTasks
    if (tasks == null || tasks.isEmpty)
      return Progress(0, 0, 0, 0, 0)
    tasks.values
      .map(_.progress())
      .reduce((left, right) => Progress(left.total + right.total, left.init + right.init, left.pending + right.pending, left.success + right.success, left.fail + right.fail))
  }

  def list(taskId: String): Iterable[String] = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    var result = Iterable.empty[String]
    try {
      if (task != null) {
        result = task.list()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def start(taskId: String): Iterable[String] = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    var result = Iterable.empty[String]
    try {
      if (task != null) {
        result = task.start()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def pause(taskId: String): Iterable[String] = {
    val task = ConfigManager.getTasks.get(taskId).orNull

    var result = Iterable.empty[String]
    try {
      if (task != null) {
        result = task.pause()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def stop(taskId: String): Iterable[String] = {
    val task = ConfigManager.getTasks.get(taskId).orNull

    var result = Iterable.empty[String]
    try {
      if (task != null) {
        result = task.stop()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def reset(taskId: String): Iterable[String] = {
    val task = ConfigManager.getTasks.get(taskId).orNull

    var result = Iterable.empty[String]
    try {
      if (task != null) {
        result = task.reset()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def exit(taskId: String): Iterable[String] = {
    val task = ConfigManager.getTasks.get(taskId).orNull

    var result = Iterable.empty[String]
    try {
      if (task != null) {
        result = task.exit()
      }
    } catch {
      case e: Exception =>
        e.printStackTrace()
        return result
    }

    result
  }

  def progress(taskId: String): Progress = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    if (task != null)
      task.progress()
    else
      Progress(0, 0, 0, 0, 0)
  }

  def list(taskId: String, siteId: String): String = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    if (task != null) {
      task.list(siteId)
    } else
      s"the task $taskId is not exists!"
  }

  def start(taskId: String, siteId: String): String = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    if (task != null) {
      task.start(siteId)
    } else
      s"the task $taskId is not exists!"
  }

  def pause(taskId: String, siteId: String): String = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    if (task != null) task.pause(siteId)
    else s"the task $taskId is not exists!"
  }

  def stop(taskId: String, siteId: String): String = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    if (task != null) task.stop(siteId)
    else s"the task $taskId is not exists!"
  }

  def reset(taskId: String, siteId: String): String = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    if (task != null) task.reset(siteId)
    else s"the task $taskId is not exists!"
  }

  def progress(taskId: String, siteId: String): Progress = {
    val task = ConfigManager.getTasks.get(taskId).orNull
    if (task != null)
      task.progress(siteId)
    else
      Progress(0, 0, 0, 0, 0)
  }

  def submit(siteConfig: SiteConfig): Unit = {
    ConfigManager.submit(siteConfig)
  }

  def submit(taskConfig: TaskConfig): Unit = {
    ConfigManager.submit(taskConfig)
  }

  def submitSite(content: String): Unit = {
    val siteConfig = JsonUtils.fromJson(content, classOf[SiteConfig])

    ConfigManager.submit(siteConfig)
  }

  def submitTask(content: String): Unit = {
    val taskConfig = JsonUtils.fromJson(content, classOf[TaskConfig])

    ConfigManager.submit(taskConfig)
  }
}
