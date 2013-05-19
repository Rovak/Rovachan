package rovachan.core

case class Comment() {

  var author: String = ""
  var text: String = ""
  var image: String = ""
  var imageExt: String = ""
  def imageName = s"$image$imageExt"
  var time: Int = 0
  var thread: Thread = null

  def thumbName = s"${image}s.jpg"
  def imgFolder = s"${thread.board.id}/${thread.id}/"
  def localImageUrl = s"$imgFolder$imageName"

}