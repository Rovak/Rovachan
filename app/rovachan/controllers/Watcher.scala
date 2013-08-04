package rovachan.controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.libs.json.Json
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import rovachan.actors.{Refresh, ThreadWatcher, AddThread}
import scala.concurrent.duration._
import rovachan.Context._
import rovachan.core.{Thread, Board}

/**
 * Watcher Control
 */
object Watcher extends Controller {

  val watcherActor = Akka.system.actorOf(Props[ThreadWatcher])

  Akka.system.scheduler.schedule(0 second, 10 second, watcherActor, Refresh())

  def addThread() = Action(parse.json) { request =>

    try {

      val threadId = (request.body \ "thread").as[String]
      val thread = Thread(threadId)
      thread.board = Board((request.body \ "board").as[String])

      watcherActor ! AddThread(thread)

      Ok(Json.obj("result" -> s"added thread $threadId"))
    } catch {
      case e: Exception => Ok(Json.obj("error" -> e.getMessage))
    }
  }
}