package rovachan.actors

import akka.actor.Actor
import rovachan.chan.fourchan.Fourchan
import rovachan.core.Board
import org.apache.commons.io.FileUtils
import java.io.File
import rovachan.Env

case class Refresh()
case class AddThread(thread: rovachan.core.Thread)

class ThreadWatcher extends Actor {

  var threads = List[rovachan.core.Thread]()
  val liveActor = LiveActor.actor

  def refreshThreads() = {
    for (thread <- threads) {
      FileUtils.copyDirectory(new File(Env.cacheFolder, thread.imgFolder), new File(Env.archiveFolder, thread.imgFolder))
    }
  }

  def addThread(thread: rovachan.core.Thread) = {
    threads ::= thread
    liveActor ! UpdateStatus(s"Watching thread ${thread.id}")
  }

  def receive = {
    case Refresh() => refreshThreads()
    case AddThread(thread) => addThread(thread)
  }
}