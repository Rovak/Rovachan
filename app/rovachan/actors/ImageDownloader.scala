package rovachan.actors

import java.io.File
import java.net.URL

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils

import akka.actor.Actor
import play.api.Play
import play.api.Play.current

case class DownloadImage(url: String)

class ImageDownloader extends Actor {

  System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1468.0 Safari/537.36");

  val basePath = Play.application.path.getAbsolutePath() + "/public/data/"

  def downloadImage(url: String) = {
    val fileName = basePath + FilenameUtils.getName(url)
    val targetFile = new File(fileName)
    if (!targetFile.exists) {
      context.actorFor("../live") ! UpdateStatus("Downloading file: " + targetFile.getName)
      FileUtils.copyURLToFile(new URL(url), targetFile)
      context.actorFor("../live") ! UpdateImage("/data/img/" + targetFile.getName)
    }
  }

  def receive = {
    case DownloadImage(url) => downloadImage(url)
  }

}