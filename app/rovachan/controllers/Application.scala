package rovachan.controllers

import scala.concurrent.Future
import akka.actor.Props
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.ws.Response
import play.api.libs.ws.WS
import play.api.mvc.Action
import play.api.mvc.Controller
import rovachan.chan.fourchan.Fourchan
import rovachan.actors.ImageDownloader
import rovachan.actors.DownloadImage
import play.api.libs.json.Reads
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Application extends Controller {

  var downloadActor = Akka.system.actorOf(Props[ImageDownloader])

  def index = Action {
    Ok(views.html.app())
  }

  def scrapeThread(url: String) = {
    var comments = List[rovachan.core.Comment]()

    WS.url(url).get().map { response =>

      response.json \\ "posts" map {
        case JsArray(posts) =>
          for (post <- posts) {
            var comment = new rovachan.core.Comment
            comment.text = (post \ "com").as[String]
            comments ::= comment
          }
      }
    }
  }

  def threads = Action {

    Async {

      var threads = List[rovachan.core.Thread]()
      var boards = Fourchan.getBoards()

      WS.url("http://api.4chan.org/wg/threads.json").get().map { response =>

        response.json \\ "threads" map {
          case JsArray(elements) =>
            for (element <- elements) {
              var thread = new rovachan.core.Thread
              thread.id = (element \ "no").as[Int].toString
              thread.url = s"http://api.4chan.org/wg/res/${thread.id}.json"
              threads ::= thread
            }
        }

        Ok(views.html.threads(threads, boards))

      }
    }
  }

  def board(boardName: String) = Action {

    Async {

      var threads = List[rovachan.core.Thread]()
      var boards = Fourchan.getBoards()

      WS.url(s"http://api.4chan.org/$boardName/catalog.json").get().map { response =>

        response.json \\ "threads" map {
          case JsArray(elements) =>
            for (element <- elements) {
              var thread = new rovachan.core.Thread
              thread.id = (element \ "no").as[Int].toString
              thread.url = s"http://boards.4chan.org/$boardName/res/${thread.id}"

              var firstComment = new rovachan.core.Comment
              firstComment.text = (element \ "com").asOpt[String].getOrElse[String]("")

              val fileName = s"${(element \ "tim").asOpt[Long].getOrElse[Long](0)}s.jpg"

              firstComment.image = s"http://0.thumbs.4chan.org/$boardName/thumb/$fileName"

              downloadActor ! DownloadImage(firstComment.image)

              thread.comments ::= firstComment

              threads ::= thread
            }
        }

        Ok(views.html.board(threads))

      }
    }
  }
}