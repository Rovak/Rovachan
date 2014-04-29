package rovachan.chan.fourchan

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.DurationInt
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.ws.WS
import rovachan.core.Board
import rovachan.core.Comment
import play.api.Play.current
import play.api.cache.Cache
import akka.util.Timeout

class Fourchan {

  val name = "4chan"

  val url = "http://api.4chan.org/boards.json"

  implicit val timeout = Timeout(5 seconds)

  def boards = Cache.getOrElse("fourchan.boards") {
    WS.url(url).get().map { response =>
      response.json \\ "boards" flatMap {
        case JsArray(elements) =>
          elements.map { element =>
            Board(
              (element \ "board").as[String],
              (element \ "title").as[String],
              s"http://boards.4chan.org/${(element \ "board").as[String]}/",
              s"http://api.4chan.org/${(element \ "board").as[String]}/catalog.json")
          }
      }
    }
  }

  def threads(board: Board): Future[Seq[rovachan.core.Thread]] = {
    WS.url(s"http://api.4chan.org/${board.id}/catalog.json").get().map { response =>
      response.json \\ "threads" flatMap {
        case JsArray(elements) =>
          elements.map { element =>
            val id = (element \ "no").as[Int].toString
            val parentThread = rovachan.core.Thread(
              (element \ "no").as[Int].toString,
              board,
              (element \ "time").as[Int])
            parentThread.comments ::= {
              val tim = (element \ "tim").asOpt[Long]
              val comment = Comment(
                (element \ "com").asOpt[String].getOrElse[String](""),
                parentThread)
              (element \ "tim").asOpt[Long].map { tim =>
                comment.image = tim.toString
                comment.imageExt = (element \ "ext").as[String]
              }
              comment
            }
            parentThread
          }
      }
    }
  }

  def thread(id: String, board: Board): Future[rovachan.core.Thread] = {
    val threadObj = rovachan.core.Thread(id, board)
    WS.url(s"http://api.4chan.org/${board.id}/res/$id.json").get().map { response =>
      response.json \\ "posts" flatMap {
        case JsArray(elements) =>
          elements.map { element =>
            var comment = Comment(
              (element \ "com").asOpt[String].getOrElse(""),
              threadObj)
            comment.time = (element \ "time").as[Int]
            comment.author = (element \ "name").asOpt[String].getOrElse("unknown")
            (element \ "tim").asOpt[Long].map { tim =>
              comment.image = tim.toString
              comment.imageExt = (element \ "ext").as[String]
            }
            threadObj.comments ::= comment
          }
      }
      threadObj.comments = threadObj.comments.sortBy(_.time)
      threadObj
    }
  }
}