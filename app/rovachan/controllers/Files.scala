package rovachan.controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import play.api.Play.current
import play.api.Play
import java.io.File
import org.apache.commons.io.FilenameUtils

object Files extends Controller {

  val projectRoot = Play.application.path

  def img(path: String) = Action {
    Ok.sendFile(new File(projectRoot + "/public/data/" + path), true)
  }
}