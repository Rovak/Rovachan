package rovabot.chan.fourchan

import rovachan.core.Board
import scala.concurrent._
import scala.concurrent.duration._
import play.api.libs.ws.WS
import play.api.libs.json.JsObject
import play.api.libs.json.JsArray
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Await

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
            boards ::= Board(
              (element \ "title").as[String],
              s"http://boards.4chan.org/${(element \ "board").as[String]}/",
              s"http(s)://api.4chan.org/${(element \ "board").as[String]}/catalog.json")

          }
        case JsObject(obj) => println("object")
      }
    }

    Await.result(req, timeout)

    boards
  }
}