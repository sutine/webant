package org.webant.commons.link

import org.webant.commons.entity.Link

case class Progress(total: Long, init: Long, pending: Long, success: Long, fail: Long)

trait ILinkProvider {
  protected var batch = 20

  def init(params: java.util.Map[String, Object]): Boolean = false

  def read(): Iterable[Link] = Iterable.empty

  def write(link: Link): Int = 0

  def write(links: Iterable[Link]): Int = 0

  def close(): Boolean = false

  def reset(status: String): Int = 0

  def progress(): Progress = null

  def total(): Long = 0

  def count(status: String): Long = 0
}
