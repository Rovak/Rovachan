package rovachan.chan.fourchan

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.ws.WS
import rovachan.core.Board

object Fourchan {

  implicit val timeout = 5 seconds

  val BOARDS_URL = "http://api.4chan.org/boards.json"

  protected var boards = List[Board]()

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
}