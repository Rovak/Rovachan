package rovachan.controllers

import akka.routing._
import akka.routing.RoundRobinRouter
import akka.actor.Props
import akka.actor.actorRef2Scala
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray
import play.api.libs.ws.WS
import play.api.mvc.Action
import play.api.mvc.Controller
import rovachan.actors.DownloadImageUrl
import rovachan.actors.ImageDownloader
import rovachan.chan.fourchan.Fourchan
import rovachan.core.Board
import rovachan.core.Comment
import play.api.mvc.WebSocket
import play.api.libs.json.JsValue
import rovachan.actors.DownloadImage
import rovachan.actors.DownloadImageThumb

object Application extends Controller {

  val chan = new Fourchan

  var downloadActor = Akka.system.actorOf(Props[ImageDownloader].withRouter(
    RoundRobinRouter(nrOfInstances = 5)), "imageDownloader")

  def index = Action {
    Ok(views.html.app())
  }

  /**
   * Retrieve the thread from the given url
   */
  def thread(site: String, board: String, id: String) = Action.async {

    chan.thread(id, Board(board)).map { thread =>
      thread.comments map (comment => downloadActor ! DownloadImage(comment))
      Ok(views.html.thread(thread))
    }
  }

  def board(boardId: String) = Action.async {
    chan.threads(Board(boardId)).map { threads =>
      threads map (thread => downloadActor ! DownloadImageThumb(thread.comments(0)))
      Ok(views.html.board(threads))
    }
  }

  /**
   * Registers a new socket which will listen to broadcasted messages
   */
  def live = WebSocket.async[JsValue] { request =>
    rovachan.actors.LiveActor.join()
  }
}