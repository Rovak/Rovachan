package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.libs.json.Json
import rovabot.chan.fourchan.Fourchan

object Data extends Controller {

  def boards = Action {

    var jsonBoards = Json.arr()

    var fourchanBoard = Json.arr()

    Fourchan.getBoards() foreach { board =>
      fourchanBoard = fourchanBoard :+ Json.obj(
        "text" -> board.title,
        "url" -> board.url,
        "leaf" -> true)
    }

    jsonBoards = jsonBoards :+ Json.obj(
      "text" -> "4chan",
      "children" -> fourchanBoard,
      "expanded" -> true)

    Ok(Json.obj("text" -> "Boards", "children" -> jsonBoards))
  }

}