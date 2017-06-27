package org.webant.worker.store

import org.webant.worker.http.HttpDataEntity

trait IStore[T <: HttpDataEntity] {
  def init(params: java.util.Map[String, Object]): Boolean = ???

  def get(id: String): T = ???
  def get(ids: Iterable[String]): Iterable[T] = ???

  def save(data: T): Int = ???
  def save(data: Iterable[T]): Int = ???

  def update(data: T): Int = ???
  def update(data: Iterable[T]): Int = ???

  def upsert(data: T): Int = ???
  def upsert(data: Iterable[T]): Int = ???

  def delete(id: String): T = ???
  def delete(ids: Iterable[String]): Iterable[T] = ???

  def close(): Boolean = ???
}
