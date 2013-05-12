package rovachan.actors

import akka.actor.Actor

case class Refresh()
case class AddThread(thread: rovachan.core.Thread)

class ThreadWatcher extends Actor {

  var threads = List[rovachan.core.Thread]()

  def refreshThreads() = {

  }

  def addThread(thread: rovachan.core.Thread) = {
    threads ::= thread
  }

  def receive = {
    case Refresh() =>
      refreshThreads()
    case AddThread(thread) =>
      addThread(thread)
  }
}