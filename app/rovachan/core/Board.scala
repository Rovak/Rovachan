package rovachan.core

import play.api.libs.json.Json
import play.api.libs.json.Json._

case class Board(id: String, var title: String = null, var url: String = null) {

  if (title == null) title = id

  var apiUrl: String = null

  def toJson = Json.obj(
    "text" -> title,
    "id" -> id,
    "url" -> url)

}