package rovachan.chan.fourchan

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.ws.WS
import rovachan.core.Board
import rovachan.core.Comment

object Fourchan {

  implicit val timeout = 5 seconds

  val BOARDS_URL = "http://api.4chan.org/boards.json"

  protected var boards = List[Board]()

  /**
   * Retrieve all boards from 4chan
   */
  def getBoards(): List[Board] = {

    if (!boards.isEmpty)
      return boards

    println("searching")
    var req = WS.url(BOARDS_URL).get().map { response =>
      response.json \\ "boards" map {
        case JsArray(elements) =>
          for (element <- elements) {
            var board = Board(
              (element \ "board").as[String],
              (element \ "title").as[String],
              s"http://boards.4chan.org/${(element \ "board").as[String]}/")

            board.apiUrl = s"http(s)://api.4chan.org/${(element \ "board").as[String]}/catalog.json"
            boards ::= board

          }
      }
    }

    Await.result(req, timeout)

    boards
  }

  /**
   * Retrieve all threads from the given board
   */
  def getThreads(boardObj: Board): List[rovachan.core.Thread] = {
    var threads = List[rovachan.core.Thread]()
    val req = WS.url(s"http://api.4chan.org/${boardObj.id}/catalog.json").get().map { response =>

      response.json \\ "threads" map {
        case JsArray(elements) => {
          for (element <- elements) {
            var parentThread = new rovachan.core.Thread((element \ "no").as[Int].toString) {
              board = boardObj
              time = (element \ "time").as[Int]
              url = s"http://boards.4chan.org/${board.id}/res/$id"
            }
            parentThread.comments ::= {
              var tim = (element \ "tim").asOpt[Long]
              new rovachan.core.Comment {
                thread = parentThread
                text = (element \ "com").asOpt[String].getOrElse[String]("")
                if (tim.isDefined) {
                  image = tim.get.toString
                  imageExt = (element \ "ext").as[String]
                }
              }
            }
            threads ::= parentThread
          }
        }
      }
    }

    Await.result(req, timeout)

    threads.sortBy(_.time)
  }

  /**
   * Retrieve a thread
   *
   * @param thread id
   * @param board to which the thread belongs
   * @return thread object with comments
   */
  def getThread(threadId: String, board: Board): rovachan.core.Thread = {
    var threadObj = rovachan.core.Thread(threadId)
    threadObj.board = board

    val req = WS.url(s"http://api.4chan.org/${board.id}/res/$threadId.json").get().map { response =>

      response.json \\ "posts" map {
        case JsArray(elements) =>
          for (element <- elements) {
            var comment = new Comment {
              thread = threadObj
              time = (element \ "time").as[Int]
              text = (element \ "com").asOpt[String].getOrElse("")
              author = (element \ "name").asOpt[String].getOrElse("unknown")
            }

            var tim = (element \ "tim").asOpt[Long]
            if (tim.isDefined) {
              comment.image = tim.get.toString
              comment.imageExt = (element \ "ext").as[String]
            }
            threadObj.comments ::= comment
          }
      }
    }
    Await.result(req, timeout)

    threadObj.comments = threadObj.comments.sortBy(_.time)
    threadObj
  }
}