package rovachan.actors

import java.io.File
import java.net.URL
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import akka.actor.Actor
import play.api.Play
import play.api.Play.current
import rovachan.core.Comment
import rovachan.controllers.routes
import play.api.libs.concurrent.Akka

case class DownloadImageUrl(url: String)
case class DownloadImage(comment: Comment)
case class DownloadImageThumb(comment: Comment)

class ImageDownloader extends Actor {

  System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1468.0 Safari/537.36");

  val basePath = Play.application.path.getAbsolutePath() + "/data/"

  def downloadImage(url: String) = {
    val fileName = basePath + FilenameUtils.getName(url)
    val targetFile = new File(fileName)
    if (!targetFile.exists) {
      Akka.system.actorFor("/user/live") ! UpdateStatus("Downloading file: " + targetFile.getName)
      FileUtils.copyURLToFile(new URL(url), targetFile)
      Akka.system.actorFor("/user/live") ! UpdateImage("/data/img/" + targetFile.getName)
    }
  }

  def downloadImageThumb(comment: Comment) = {
    var url = s"http://0.thumbs.4chan.org/${comment.thread.board.id}/thumb/${comment.thumbName}"
    val targetFile = new File(s"$basePath${comment.imgFolder}${comment.thumbName}")
    if (!targetFile.exists) {
      Akka.system.actorFor("/user/live") ! UpdateStatus(s"Downloading file: ${comment.thumbName}")
      FileUtils.copyURLToFile(new URL(url), targetFile)
      Akka.system.actorFor("/user/live") ! UpdateImage(routes.Files.img(s"${comment.imgFolder}${comment.thumbName}").toString)
    }
  }

  /**
   * Download the image which is attached to the comment
   */
  def downloadImageFromComment(comment: Comment) = {
    var url = s"http://images.4chan.org/${comment.thread.board.id}/src/${comment.imageName}"
    val targetFile = new File(s"$basePath${comment.localImageUrl}")
    if (!targetFile.exists) {
      Akka.system.actorFor("/user/live") ! UpdateStatus("Downloading file: " + comment.imageName)
      FileUtils.copyURLToFile(new URL(url), targetFile)
      Akka.system.actorFor("/user/live") ! UpdateImage(routes.Files.img(comment.localImageUrl).toString)
    }
  }

  def receive = {
    //case DownloadImageUrl(url) => downloadImage(url)
    case DownloadImageThumb(comment) => downloadImageThumb(comment)
    case DownloadImage(comment) => downloadImageFromComment(comment)
  }

}