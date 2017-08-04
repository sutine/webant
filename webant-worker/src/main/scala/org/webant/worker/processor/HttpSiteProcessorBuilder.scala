package org.webant.worker.processor

import org.webant.commons.entity.SiteConfig
import org.webant.commons.entity.SiteConfig.{HttpConfig, ProcessorConfig}

class HttpSiteProcessorBuilder {
  private val siteConfig: SiteConfig = new SiteConfig

  def create(): Unit = {
//    new HttpSiteProcessor(siteConfig)
  }

  def id(id: String): HttpSiteProcessorBuilder = {
    siteConfig.id = id
    this
  }

  def name(name: String): HttpSiteProcessorBuilder = {
    siteConfig.name = name
    this
  }

  def description(description: String): HttpSiteProcessorBuilder = {
    siteConfig.description = description
    this
  }

  def seeds(seeds: Array[String]): HttpSiteProcessorBuilder = {
    siteConfig.seeds = seeds
    this
  }

  def interval(interval: Long): HttpSiteProcessorBuilder = {
    siteConfig.setTimeInterval(interval)
    this
  }

  def http(http: HttpConfig): HttpSiteProcessorBuilder = {
    siteConfig.http = http
    this
  }

  def processors(processors: Array[ProcessorConfig]): HttpSiteProcessorBuilder = {
    siteConfig.processors = processors
    this
  }
}
