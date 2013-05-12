package rovachan.actors

import scala.concurrent.duration.DurationInt

import akka.actor.Actor
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Concurrent
import play.api.libs.iteratee.Done
import play.api.libs.iteratee.Enumerator
import play.api.libs.iteratee.Input
import play.api.libs.iteratee.Iteratee
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper

case class Join()
case class Connected(enumerator: Enumerator[JsValue])
case class CannotConnect(msg: String)
case class Broadcast(message: String) {
  def toJson = Json.obj("message" -> message)
}
case class UpdateStatus(message: String) {
  def toJson = Json.obj(
    "action" -> "status",
    "message" -> message)
}

object LiveActor {

  implicit val timeout = Timeout(2 second)

  lazy val actor = Akka.system.actorOf(Props[LiveActor], "live")

  /**
   * Join Live Update Channel
   */
  def join(): scala.concurrent.Future[(Iteratee[JsValue, _], Enumerator[JsValue])] = {

    (actor ? Join()) map {

      case Connected(enumerator) =>

        actor ! UpdateStatus("Connected!")

        val iteratee = Iteratee.foreach[JsValue] { event =>

        }.mapDone { _ =>
          // On Connection Closed
        }

        (iteratee, enumerator)

      case CannotConnect(error) =>
        val iteratee = Done[JsValue, Unit]((), Input.EOF)
        val enumerator = Enumerator[JsValue](Json.obj("error" -> error)).andThen(Enumerator.enumInput(Input.EOF))

        (iteratee, enumerator)
    }
  }
}

/**
 * Logs messages
 */
class LiveActor extends Actor {

  val (chatEnumerator, liveChannel) = Concurrent.broadcast[JsValue]

  def receive = {
    case Join() => {
      sender ! Connected(chatEnumerator)
    }
    case Broadcast(message) => {
      liveChannel.push(Json.obj("message" -> message))
    }
    case status: UpdateStatus => {
      liveChannel.push(status.toJson)
    }
  }

}