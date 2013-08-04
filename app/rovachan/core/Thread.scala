package rovachan.core

import play.api.libs.json.Json

case class Thread(id: String) {

  var url: String = null
  var board: Board = Board("null")
  var time: Int = 0

  var comments = List[Comment]()

  def imgFolder = s"${board.id}/$id/"
  def toJson = Json.obj(
    "id" -> id,
    "url" -> url,
    "board" -> board.toJson,
    "time" -> time)

}