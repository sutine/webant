package org.webant.worker.store

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.HttpDataEntity
import org.webant.commons.entity.SiteConfig.{ProcessorConfig, StoreProvider}
import org.webant.commons.store.IStore

import scala.collection.parallel.immutable

object StoreFactory {
  private val logger = LogManager.getLogger(StoreFactory.getClass)

  var stores = immutable.ParMap.empty[String, Iterable[IStore[HttpDataEntity]]]

  def getStores(className: String): Iterable[IStore[HttpDataEntity]] = {
    if (!stores.contains(className)) return Iterable.empty
    stores(className)
  }

  def load(processorConfig: ProcessorConfig): Unit = {
    require(processorConfig != null)
    if (stores.contains(processorConfig.getClassName)) return

    if (processorConfig.getStore != null) {
      val list = getStoreList(processorConfig.getStore)
      stores += (processorConfig.getClassName, list)
    }
  }

  def getStoreList(storeConfigs: Array[StoreProvider]): Iterable[IStore[HttpDataEntity]] = {
    require(storeConfigs != null && storeConfigs.nonEmpty)

    val stores = storeConfigs.map(storeConfig => {
      getStoreProvider(storeConfig)
    })

    stores
  }

  private def getStoreProvider(config: StoreProvider): IStore[HttpDataEntity] = {
    var provider: IStore[HttpDataEntity] = null
    if (config != null && StringUtils.isNotBlank(config.getClassName)) {
      try {
        provider = Class.forName(config.getClassName).newInstance().asInstanceOf[IStore[HttpDataEntity]]
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }

    if(provider == null || !provider.init(config.getParams)) {
      logger.error("init link provider failed! use default H2Store.")
      val h2Provider = new H2Store[HttpDataEntity]()
      h2Provider.init()

      provider = h2Provider
    }

    provider
  }

}
