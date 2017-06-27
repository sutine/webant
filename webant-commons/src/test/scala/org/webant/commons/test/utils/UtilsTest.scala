package org.webant.commons.test.utils

import org.junit.{After, Before, Test}
import org.scalatest.junit.AssertionsForJUnit
import org.webant.commons.utils.Retry

class UtilsTest extends AssertionsForJUnit {

  @Before
  def init(): Unit = {
  }

  @After
  def exit() {
  }

  @Test
  def testRetry(): Unit = {
    val r = Retry(3)(add(1, 2))
    println(r)
  }

  @throws[IllegalArgumentException]
  private def add(a: Int, b: Int): Int = {
    a + b
  }
}
