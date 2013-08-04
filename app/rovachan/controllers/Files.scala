package rovachan.controllers

import java.io.File

import play.api.Play
import play.api.Play.current
import play.api.mvc.Action
import play.api.mvc.Controller
import rovachan.Env

object Files extends Controller {

  def returnFile(file: java.io.File) = {
    if (file.exists) {
      Ok.sendFile(file, true)
    } else {
      NotFound(file.getName + " not found")
    }
  }

  def cache(path: String) = Action {
    returnFile(new File(Env.cacheFolder, path))
  }

  def archive(path: String) = Action {
    returnFile(new File(Env.archiveFolder, path))
  }
}