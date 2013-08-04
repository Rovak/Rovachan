package rovachan.actors

import java.io.File
import java.net.URL
import org.apache.commons.io.FileUtils
import akka.actor.Actor
import rovachan.core.Comment
import rovachan.controllers.routes
import rovachan.Env

case class DownloadImageUrl(url: String)
case class DownloadImage(comment: Comment)
case class DownloadImageThumb(comment: Comment)

class ImageDownloader extends Actor {

  System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1468.0 Safari/537.36");

  val liveActor = LiveActor.actor

  def downloadImage(url: String, targetFile: java.io.File) = {
    if (!targetFile.exists) {
      liveActor! UpdateStatus(s"Downloading file: ${targetFile.getName} to ${targetFile.getAbsolutePath}")
      try {
        FileUtils.copyURLToFile(new URL(url), targetFile)
        liveActor! UpdateImage(routes.Files.cache(targetFile.getAbsolutePath.substring(Env.cacheFolder.size)).toString)
      } catch {
        case e: Exception => println(s"File not found: $url")
      }
    }
  }

  def downloadImageThumb(comment: Comment) = {
    val url = s"http://0.thumbs.4chan.org/${comment.thread.board.id}/thumb/${comment.thumbName}"
    val targetFile = new File(Env.cacheFolder, s"${comment.imgFolder}/${comment.thumbName}")
    downloadImage(url, targetFile)
  }

  /**
   * Download the image which is attached to the comment
   */
  def downloadImageFromComment(comment: Comment) = {
    val url = s"http://images.4chan.org/${comment.thread.board.id}/src/${comment.imageName}"
    val targetFile = new File(Env.cacheFolder, comment.localImageUrl)
    downloadImage(url, targetFile)
  }


  def receive = {
    //case DownloadImageUrl(url) => downloadImage(url)
    case DownloadImageThumb(comment) => downloadImageThumb(comment)
    case DownloadImage(comment) => downloadImageFromComment(comment)
  }

}