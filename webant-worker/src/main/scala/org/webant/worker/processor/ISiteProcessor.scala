package org.webant.worker.processor

trait ISiteProcessor {
  def accept(url: String): Boolean = ???
  def pre(): Boolean = ???
  def parse(): Unit = ???
  def data(): Unit = ???
  def links(): Iterable[String] = ???
  def store() = ???
  def post() = ???
  def close() = ???
}
