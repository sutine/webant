package org.webant.commons.store

import java.io.File

import org.apache.commons.collections.MapUtils
import org.apache.commons.io.FileUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.HttpDataEntity
import org.webant.commons.utils.JsonUtils

class JsonStore[T <: HttpDataEntity] extends IStore[T] {
  private val logger = LogManager.getLogger(classOf[JsonStore[HttpDataEntity]])

  private var dir: File = _

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("dir"))
      return false

    try {
      val dirPath = MapUtils.getString(params, "dir", "./data/json")
      dir = new File(dirPath)
      if (!dir.exists() || !dir.isDirectory) {
        dir.mkdir()
      }
    } catch {
      case e: Exception =>
        logger.error(e.getMessage())

        dir = new File("./data/json")
        if (!dir.exists() || !dir.isDirectory) {
          dir.mkdir()
        }
    }

    dir.exists() && dir.isDirectory
  }

  override def save(data: T): Int = {
    0
  }

  override def upsert(data: T): Int = {
    require(data != null)
    FileUtils.writeStringToFile(new File(dir, s"${data.srcId}.json"), JsonUtils.toJson(data), "UTF-8")
    1
  }
}
