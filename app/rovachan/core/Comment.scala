package rovachan.core

case class Comment() {

  var author: String = ""
  var text: String = ""
  var image: String = ""
  var imageExt: String = ""
  def imageName = s"$image$imageExt"
  var time: Int = 0

  def thumbName = s"${image}s.jpg"

}