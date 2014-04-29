package rovachan.controllers

import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import rovachan.chan.fourchan.Fourchan
import rovachan.core.Board
import scala.concurrent.Future

object Data extends Controller {

  val websites = List(
    new Fourchan
  )

  def boards = Action.async {

    import scala.concurrent.ExecutionContext.Implicits.global

    val result = websites.foldLeft(List(Future.successful(Json.arr()))) { case (current, website) =>
      current :+ website.boards.map { res =>
          Json.arr(Json.obj(
            "text" -> website.name,
            "children" -> res.foldLeft(Json.arr()) {
              case (boards: JsArray, board: Board) =>
                boards :+ (board.toJson ++ Json.obj("leaf" -> true))
            },
            "expanded" -> true))
      }
    }

    Future.sequence(result).map { arr =>
      val childs = arr.foldLeft(Json.arr()) { case (c, a) => c ++ a }
      Ok(Json.obj("text" -> "Boards", "children" -> childs))
    }
  }
}