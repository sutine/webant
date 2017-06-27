package org.webant.worker.store

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.worker.config.{ProcessorConfig, StoreProvider}
import org.webant.worker.http.HttpDataEntity

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
    if (stores.contains(processorConfig.className)) return

    if (processorConfig.store != null) {
      val list = getStoreList(processorConfig.store)
      stores += (processorConfig.className, list)
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
    if (config != null && StringUtils.isNotBlank(config.className)) {
      try {
        provider = Class.forName(config.className).newInstance().asInstanceOf[IStore[HttpDataEntity]]
      } catch {
        case e: Exception =>
          e.printStackTrace()
      }
    }

    if(provider == null || !provider.init(config.params)) {
      logger.error("init link provider failed! user default H2Store.")
      val h2Provider = new H2Store[HttpDataEntity]()
      h2Provider.init()

      provider = h2Provider
    }

    provider
  }

}
