package org.webant.commons.test.akka

import akka.actor._
import akka.routing.RoundRobinPool


sealed trait PiMessage
case object Calculate extends PiMessage
case class Work(start: Int, elements: Int) extends PiMessage
case class Result(value: Double) extends PiMessage
case class PiApproximation(pi: Double, duration: Long)

class CrawlerWorker extends Actor {

  //计算从start开始，连续elements个的和
  def calculatePiFor(start: Int, elements: Int): Double = {
    var acc = 0.0
    for (i <- start until (start + elements))
      acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
    acc
  }

  def receive: PartialFunction[Any, Unit] = {
    // sender 用来访问当前消息的发送者的引用
    case Work(start, elements) => sender ! Result(calculatePiFor(start, elements))
  }
}

class CrawlerMaster(workers: Int, messages: Int, elements: Int, listener: ActorRef) extends Actor {

  var pi: Double = 0.0
  var finish: Int = 0
  val startTime: Long = System.currentTimeMillis()

  //创建了一个路由器，启动了workers个Worker
  val workerRouter: ActorRef = context.actorOf(
    Props[CrawlerWorker].withRouter(RoundRobinPool(workers)), name = "workerRouter")

  def receive: PartialFunction[Any, Unit] = {

    //收到计算的请求，把计算的任务分配下去
    case Calculate =>
      for (i <- 0 until messages)
        workerRouter ! Work(i * elements, elements)
    //收到计算的结果，把计算的结果加到pi上，并且判断下发的任务有没有全部执行完毕
    //如果全部执行完毕，那么给监听者发一个消息
    case Result(value) =>
      pi += value
      finish += 1
//      if (finish == messages) {

        listener ! PiApproximation(pi, duration = System.currentTimeMillis - startTime)

//        context.stop(self)
//      }
  }
}

class CrawlerListener extends Actor {

  def receive: PartialFunction[Any, Unit] = {

    case PiApproximation(pi, duration) =>
      println("计算结束,结果为: " + pi + "用时 : " + duration)
  }

}

object Pi {

  def main(args: Array[String]) {
    calculate(16, 10000, 100000)
  }

  var master: ActorRef = _

  def calculate(workers: Int, elements: Int, messages: Int): String = {

    val system = ActorSystem("PiSystem")

    val listener = system.actorOf(Props[CrawlerListener], name = "listener")

    master = system.actorOf(Props(new CrawlerMaster(
      workers, messages, elements, listener)),
      name = "master")

    master ! Calculate
    "success"
  }

  def reCalc(): String = {
    master ! Calculate
    "success"
  }
}

