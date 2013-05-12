package rovachan.controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.libs.json.Json
import play.api.libs.concurrent.Akka
import play.api.Play.current
import akka.actor.Props
import rovachan.actors.ThreadWatcher
import play.api.libs.json.JsObject
import rovachan.actors.AddThread
import views.html.defaultpages.badRequest

/**
 * Watcher Control
 */
object Watcher extends Controller {

  val watcherActor = Akka.system.actorOf(Props[ThreadWatcher])

  def addThread = Action(parse.json) { request =>

    try {

      val threadId = (request.body \ "thread").as[String]

      watcherActor ! AddThread(rovachan.core.Thread(threadId))

      Ok(Json.obj("result" -> s"added thread $threadId"))
    } catch {
      case e: Exception => Ok(Json.obj("error" -> e.getMessage))
    }
  }
}