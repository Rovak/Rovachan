package rovachan.core

import play.api.libs.json.Json

case class Thread(id: String, board: Board, time: Int = 0) {

  var comments = List[Comment]()

  def imgFolder = s"${board.id}/$id/"
  def url = s"http://api.4chan.org/${board.id}/res/$id"
  def urlJson = url + ".json"
  def toJson = Json.obj(
    "id" -> id,
    "url" -> url,
    "board" -> board.toJson,
    "time" -> time)

}