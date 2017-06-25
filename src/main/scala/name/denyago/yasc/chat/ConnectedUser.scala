package name.denyago.yasc.chat

import akka.actor.{Actor, ActorRef}
import name.denyago.yasc.chat.events.{ConnectionEstablished, MessagePosted, MessageReceived, UserJoined}

/**
  * Actor, holding a WebSocket connection of a particular User.
  * Will become connected once WebSocket connection is established
  *
  * @param chatRoom a Chat Room User enters into
  */
class ConnectedUser(chatRoom: ActorRef) extends Actor {

  override def receive: Receive = {
    case ConnectionEstablished(outgoing) =>
      context.become(connected(outgoing))
  }

  def connected(outgoing: ActorRef): Receive = {
    chatRoom ! UserJoined(self.toString(), self)

    {
      case MessageReceived(text) =>
        chatRoom ! MessagePosted(text)
      case MessagePosted(text) =>
        outgoing ! MessagePosted(text)
    }
  }
}

