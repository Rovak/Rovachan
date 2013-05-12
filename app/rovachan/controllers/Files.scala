package rovachan.controllers

import java.io.File

import play.api.Play
import play.api.Play.current
import play.api.mvc.Action
import play.api.mvc.Controller

object Files extends Controller {

  val projectRoot = Play.application.path

  def img(path: String) = Action {
    val imgFile = new File(projectRoot + "/public/data/" + path)
    if (imgFile.exists) {
      Ok.sendFile(imgFile, true)
    } else {
      NotFound(imgFile.getName + " not found")
    }
  }
}