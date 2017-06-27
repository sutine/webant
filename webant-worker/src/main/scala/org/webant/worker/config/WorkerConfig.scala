package org.webant.worker.config

class WorkerConfig extends java.io.Serializable {
  var serverHost: String = "localhost"
  var serverPort: Int = 1099
  var threadNum: Int = 32
  var dataDir: String = "./data"
  var queen: Queen = _
  var taskMonitor: ConfigMonitor = _
  var siteMonitor: ConfigMonitor = _
}

class Queen extends java.io.Serializable {
  var url: String = "http://localhost"
}

class ConfigMonitor extends java.io.Serializable {
  var dir: String = _
  var interval: Int = _
}