package rovachan

import play.api.Play
import play.api.Play.current

object Env {
  val dataFolder = Play.application.path.getAbsolutePath + "/data"
  val cacheFolder = dataFolder + "/cache"
  val archiveFolder = dataFolder + "/archive"
}
