package org.webant.queen

import com.jfinal.core.JFinal
import org.apache.log4j.LogManager

object QueenApp {
  private val logger = LogManager.getLogger(QueenApp.getClass)

  def main(args: Array[String]) {
    JFinal.start("webant-queen/src/main/webapp", 80, "/")
    logger.info("queen app.")
  }
}
