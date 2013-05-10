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
            var comment = new rovachan.core.Comment()
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

    Async {

      var comments = List[rovachan.core.Comment]()

      WS.url(s"http://api.4chan.org/$board/res/$id.json").get().map { response =>

        println(response.body.toString())

        response.json \\ "posts" map {
          case JsArray(elements) =>
            for (element <- elements) {
              var comment = new rovachan.core.Comment()
              comment.time = (element \ "time").as[Int]
              comment.text = (element \ "com").as[String]
              comment.author = (element \ "name").as[String]
              val fileName = s"${(element \ "tim").asOpt[Long].getOrElse[Long](0)}s.jpg"
              comment.image = fileName
              comments ::= comment
            }
        }

        comments map (comment => downloadActor ! DownloadImage(comment.image))

        Ok(views.html.thread(comments.sortBy(_.time)))
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
              var thread = new rovachan.core.Thread()
              thread.id = (element \ "no").as[Int].toString
              thread.url = s"http://boards.4chan.org/$boardName/res/${thread.id}"
              thread.comments ::= {
                var firstComment = new rovachan.core.Comment
                firstComment.text = (element \ "com").asOpt[String].getOrElse[String]("")
                val fileName = s"${(element \ "tim").asOpt[Long].getOrElse[Long](0)}s.jpg"
                firstComment.image = s"http://0.thumbs.4chan.org/$boardName/thumb/$fileName"
                firstComment
              }

              threads ::= thread
            }
        }

        threads map (thread => downloadActor ! DownloadImage(thread.comments(0).image))

        Ok(views.html.board(threads))

      }
    }
  }
}