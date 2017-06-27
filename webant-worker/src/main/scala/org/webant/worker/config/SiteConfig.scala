package org.webant.worker.config

class SiteConfig extends java.io.Serializable {
  var id: String = _
  var name: String =_
  var description: String = _
  var seeds: Array[String] = _
  var priority: Int = 4
  var interval: Long = 0L
  var incrementInterval: Long = 0L
  var http: HttpConfig = _
  var linkProvider: LinkProvider = _
  var processors: Array[ProcessorConfig] = _
}

class HttpConfig extends java.io.Serializable {
  var method: String = _
  var connectTimeout: Int = _
  var socketTimeout: Int = _
  var encoding: String = _
  var retryTimes: Int = _
  var cycleRetryTimes: Int = _
  var body: String = _
  var contentType: String = _
  var proxy: Boolean = _
  var headers: java.util.Map[String, String] = _
}

class ProcessorConfig extends java.io.Serializable {
  var regex: String = _
  var http: HttpConfig = _
  var className: String = _
  var store: Array[StoreProvider] = _
}

class LinkProvider extends java.io.Serializable {
  var className: String = _
  var params: java.util.Map[String, Object] = _
}

class StoreProvider extends java.io.Serializable {
  var className: String = _
  var params: java.util.Map[String, Object] = _
}