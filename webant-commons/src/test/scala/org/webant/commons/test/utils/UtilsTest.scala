package org.webant.commons.test.utils

import junit.framework.TestCase
import org.junit.{After, Before, Test}
import org.webant.commons.utils.Retry

class UtilsTest extends TestCase {

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
