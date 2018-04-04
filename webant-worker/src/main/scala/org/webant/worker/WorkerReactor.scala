package org.webant.worker

import akka.actor._
import akka.routing.RoundRobinPool
import org.apache.commons.lang3.StringUtils
import org.apache.log4j.LogManager
import org.webant.commons.entity.{HttpDataEntity, Link}
import org.webant.worker.config.ConfigManager
import org.webant.worker.http.HttpResponse


sealed trait WorkerMessage
case class LinksMessage(links: Iterable[Link]) extends WorkerMessage
case class LinkMessage(link: Link) extends WorkerMessage
case class ResponseMessage(srcLink: Link, resp: HttpResponse[HttpDataEntity]) extends WorkerMessage
case class ResultMessage(link: Link, resp: HttpResponse[HttpDataEntity])

class Worker extends Actor {

  def receive: PartialFunction[Any, Unit] = {
    case LinkMessage(link) =>
      sender ! ResponseMessage(link, extract(link))
  }

  private def extract(link: Link): HttpResponse[HttpDataEntity] = {
    if (link == null || StringUtils.isBlank(link.getTaskId)
      || StringUtils.isBlank(link.getSiteId) || StringUtils.isBlank(link.getUrl))
      return null

    val task = ConfigManager.getTaskManager(link.getTaskId)
    if (task == null) return null

    val site = task.getSiteManager(link.getSiteId)
    if (site == null) return null

    val processor = site.getSiteProcessor
    if (processor == null) return null

    val resp = processor.process(link)

    resp
  }
}

class Master(workerNum: Int, listener: ActorRef) extends Actor {
  // create a router
  val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinPool(workerNum)), name = "workerRouter")

  def receive = {
    case LinksMessage(links) =>
      links.foreach(link => {
        workerRouter ! LinkMessage(link)
      })

    case LinkMessage(link) =>
      workerRouter ! LinkMessage(link)

    // process complete, send a message to listener
    case ResponseMessage(srcLink, content) =>
        listener ! ResultMessage(srcLink, content)
  }
}

class Listener extends Actor {
  def receive = {
    case ResultMessage(srcLink, resp) =>
      if (resp != null && resp.links != null && !resp.links.isEmpty) {
        srcLink.setStatus(Link.LINK_STATUS_SUCCESS)
      } else {
        srcLink.setStatus(Link.LINK_STATUS_FAIL)
      }
  }
}

class WorkerReactor() {

}

object WorkerReactor {
  private val logger = LogManager.getLogger(WorkerReactor.getClass)

  var master: ActorRef = _
  var system: ActorSystem = _

  def apply(): WorkerReactor = {
    new WorkerReactor()
  }

  def start(): Unit = {
    if (master == null) {
      system = ActorSystem("WebantWorkerActor")
      val listener = system.actorOf(Props[Listener], name = "listener")

      val workerNum = ConfigManager.getWorkerConfig.threadNum
      master = system.actorOf(Props(new Master(workerNum, listener)), name = "master")

      logger.info("webant worker start successful.")
    }
  }

  def submit(links: Iterable[Link]): Unit = {
    if (links == null || links.isEmpty) return
    master ! LinksMessage(links)
  }

  def submit(link: Link): Unit = {
    if (link == null) return
    master ! LinkMessage(link)
  }

  def exit(): Unit = {
    if (system != null)
      system.shutdown()
  }
}

