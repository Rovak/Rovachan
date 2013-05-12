package rovachan.core

case class Thread(id: String) {

  var url: String = null
  var board: Board = null
  var time: Int = 0

  var comments = List[Comment]()
}