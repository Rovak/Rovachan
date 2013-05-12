package rovachan.controllers

import akka.actor.Props
import akka.actor.actorRef2Scala
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray
import play.api.libs.ws.WS
import play.api.mvc.Action
import play.api.mvc.Controller
import rovachan.actors.DownloadImage
import rovachan.actors.ImageDownloader
import rovachan.chan.fourchan.Fourchan
import rovachan.core.Board
import rovachan.core.Comment
import play.api.mvc.WebSocket
import play.api.libs.json.JsValue

object Application extends Controller {

  var downloadActor = Akka.system.actorOf(Props[ImageDownloader], "imageDownloader")

  def index = Action {
    Ok(views.html.app())
  }

  def scrapeThread(url: String) = {
    var comments = List[Comment]()

    WS.url(url).get().map { response =>

      response.json \\ "posts" map {
        case JsArray(posts) =>
          for (post <- posts) {
            var comment = Comment()
            comment.text = (post \ "com").as[String]
            comments ::= comment
          }
      }
    }
  }

  /**
   * Retrieve the thread from the given url
   */
  def thread(site: String, board: String, id: String) = Action {

    var thread = Fourchan.getThread(id, Board(board))
    thread.comments map (comment => downloadActor ! DownloadImage(s"http://images.4chan.org/${thread.board.id}/src/${comment.imageName}"))

    Ok(views.html.thread(thread))

  }

  def board(boardId: String) = Action {

    var threads = Fourchan.getThreads(Board(boardId))

    threads map (thread => downloadActor ! DownloadImage(s"http://0.thumbs.4chan.org/${thread.board.id}/thumb/${thread.comments(0).thumbName}"))
    //threads map (thread => downloadActor ! DownloadImage(s"http://images.4chan.org/${thread.board.id}/src/${thread.comments(0).imageName}"))

    Ok(views.html.board(threads))

  }

  /**
   * Registers a new socket which will listen to broadcasted messages
   */
  def live = WebSocket.async[JsValue] { request =>
    rovachan.actors.LiveActor.join()
  }
}