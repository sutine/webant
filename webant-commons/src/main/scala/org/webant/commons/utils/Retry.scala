package org.webant.commons.utils

object Retry {
  // Returning T, throwing the exception on failure
  def apply[T](retryTimes: Int)(fn: => T): T = {
    util.Try { fn } match {
      case util.Success(x) => x
      case _ if retryTimes > 1 => apply(retryTimes - 1)(fn)
      case util.Failure(e) => throw e
    }
  }

  // Returning a Try[T] wrapper
/*
  @annotation.tailrec
  def retry[T](retryTimes: Int)(fn: => T): util.Try[T] = {
    util.Try { fn } match {
      case x: util.Success[T] => x
      case _ if retryTimes > 1 => retry(retryTimes - 1)(fn)
      case fn => fn
    }
  }
*/
}
