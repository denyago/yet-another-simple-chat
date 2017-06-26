package name.denyago.yasc.chat

import akka.actor.ActorRef

package object events {
  case class UserJoined(name: String, actorRef: ActorRef)

  case class MessagePosted(text: String)

  case class ConnectionEstablished(outgoing: ActorRef)

  case class MessageReceived(text: String)
}
