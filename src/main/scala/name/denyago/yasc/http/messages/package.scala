package name.denyago.yasc.http

import akka.actor.ActorRef

package object messages {
  case object ServerStarted
  case object ServerStopped
  case object ServerFailed

  case class ChatRoomStarted(chatRoom: ActorRef)
  case class RetrySeverStart(tryCount: Int)
}
