package org.webant.extension.store

import java.io.File

import org.apache.commons.collections.MapUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.HttpDataEntity
import org.webant.extension.berkeleydb.{BerkeleydbDao, BerkeleydbDatabaseObjectImpl}
import org.webant.commons.store.IStore

class BerkeleyDbStore[T <: HttpDataEntity] extends IStore[T] {
  private val logger = LogManager.getLogger(classOf[BerkeleyDbStore[HttpDataEntity]])

  private var dir: File = _
  private var dao: BerkeleydbDao[T] = _
  private val dbName: String = "webant"

  override def init(params: java.util.Map[String, Object]): Boolean = {
    if (!params.containsKey("dir"))
      return false

    try {
      val dirPath = MapUtils.getString(params, "dir", "./data/bdb")
      dir = new File(dirPath)
      if (!dir.exists() || !dir.isDirectory) {
        dir.mkdir()
      }
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)

        dir = new File("./data/bdb")
        if (!dir.exists() || !dir.isDirectory) {
          dir.mkdir()
        }
    }

    if (!dir.exists()) {
      logger.error(s"the parameter dir does not exists.")
      return false
    }
    if (!dir.isDirectory) {
      logger.error(s"the parameter dir is not a directory.")
      return false
    }

    dao = new BerkeleydbDatabaseObjectImpl[T]
    dao.openConnection(dir.getAbsolutePath, dbName)

    dao != null
  }

  override def save(data: T): Int = {
    0
  }

  override def upsert(data: T): Int = {
    require(data != null)
    dao.save(data.id, data)
    1
  }
}
