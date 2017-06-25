package name.denyago.yasc.chat

import akka.actor.{Actor, ActorRef, Terminated}
import name.denyago.yasc.chat.events.{MessagePosted, UserJoined}

/**
  * Actor, representing Chat Room, where Users may join, leave and
  * exchange messages.
  */
class ChatRoom extends Actor {
  override def receive = withUsers(Map.empty)

  def withUsers(users: Map[ActorRef, String]): Receive = {
    case UserJoined(name, actorRef) =>
      context.watch(sender())
      context.become(withUsers(users.updated(actorRef, name)))

    case Terminated(actorRef) =>
      context.unwatch(actorRef)
      context.become(withUsers(users.filterKeys(_ != actorRef)))

    case MessagePosted(text) =>
      users.keys.foreach(_ ! MessagePosted(text))
  }
}
