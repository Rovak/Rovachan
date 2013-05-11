package rovachan.controllers

import play.api.libs.json.JsArray
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.mvc.Action
import play.api.mvc.Controller
import rovachan.chan.fourchan.Fourchan
import rovachan.core.Board

object Data extends Controller {

  def boards = Action {

    val fourchanBoard = Fourchan.getBoards().foldLeft(Json.arr()) {
      (boards: JsArray, board: Board) =>
        boards :+ (board.toJson ++ Json.obj("leaf" -> true))
    }

    val jsonBoards = Json.arr(
      Json.obj(
        "text" -> "4chan",
        "children" -> fourchanBoard,
        "expanded" -> true))

    Ok(Json.obj("text" -> "Boards", "children" -> jsonBoards))
  }

}