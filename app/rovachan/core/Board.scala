package rovachan.core

import play.api.libs.json.Json
import views.html.board

class Board {

  var title: String = null
  var url: String = null
  var apiUrl: String = null
  var id: String = null

  def toJson = Json.obj(
    "text" -> title,
    "id" -> id,
    "url" -> url)

}