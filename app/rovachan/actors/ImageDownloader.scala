package rovachan.actors

import akka.actor.Actor
import org.apache.commons.io.FileSystemUtils
import org.apache.commons.io.FileUtils
import java.net.URL
import java.io.File
import org.apache.commons.io.FilenameUtils
import play.api.Play
import sys.process._
import java.net.URL
import java.io.File
import java.io.IOException
import java.io.FileOutputStream
import java.io.BufferedInputStream
import play.api.Play.current

case class DownloadImage(url: String)

class ImageDownloader extends Actor {

  System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1468.0 Safari/537.36");

  def downloadImage(url: String) = {
    val fileName = Play.application.path.getAbsolutePath() + "/public/data/" + FilenameUtils.getName(url)
    val targetFile = new File(fileName)
    if (!targetFile.exists) {
      FileUtils.copyURLToFile(new URL(url), targetFile)
    }
  }

  def receive = {
    case DownloadImage(url) => downloadImage(url)
  }

}