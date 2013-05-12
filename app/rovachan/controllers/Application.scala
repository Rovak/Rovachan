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

object Application extends Controller {

  var downloadActor = Akka.system.actorOf(Props[ImageDownloader])

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

    Async {

      var comments = List[Comment]()

      WS.url(s"http://api.4chan.org/$board/res/$id.json").get().map { response =>

        println(response.body.toString())

        response.json \\ "posts" map {
          case JsArray(elements) =>
            for (element <- elements) {
              var comment = new Comment()
              comment.time = (element \ "time").as[Int]
              comment.text = (element \ "com").asOpt[String].getOrElse("")
              comment.author = (element \ "name").as[String]

              var tim = (element \ "tim").asOpt[Long]
              if (tim.isDefined) comment.image = s"${tim.get}s.jpg"

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
              var thread = rovachan.core.Thread((element \ "no").as[Int].toString)
              thread.board = Board(boardName)
              thread.time = (element \ "time").as[Int]
              thread.url = s"http://boards.4chan.org/$boardName/res/${thread.id}"
              thread.comments ::= {
                var firstComment = new rovachan.core.Comment
                firstComment.text = (element \ "com").asOpt[String].getOrElse[String]("")
                var tim = (element \ "tim").asOpt[Long]
                if (tim.isDefined) firstComment.image = s"${tim.get}s.jpg"
                firstComment
              }

              threads ::= thread
            }
        }

        threads map (thread => downloadActor ! DownloadImage(thread.comments(0).image))

        Ok(views.html.board(threads.sortBy(_.time)))
      }
    }
  }
}