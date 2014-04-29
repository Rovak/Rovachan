package rovachan.core

case class Comment(text: String, thread: Thread) {

  var author: String = ""
  var image: String = ""
  var imageExt: String = ""
  def imageName = s"$image$imageExt"
  var time: Int = 0

  def thumbName = s"${image}s.jpg"
  def imgFolder = thread.imgFolder
  def localImageUrl = s"$imgFolder$imageName"

}