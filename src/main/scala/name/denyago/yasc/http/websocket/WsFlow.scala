package name.denyago.yasc.http.websocket

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import name.denyago.yasc.chat.ConnectedUser
import name.denyago.yasc.chat.events.{ConnectionEstablished, MessagePosted, MessageReceived}

/**
  * Contains a builder function for WebSocket messages flow.
  * This flow allows to register new Users in the ChatRoom
  * and pass text messages.
  */
trait WsFlow {
  implicit val webSocketSys = ActorSystem("websocket-system")
  implicit val webSocketMat = ActorMaterializer()

  def chatRoom: ActorRef

  def wsFlow: Flow[Message, Message, NotUsed] = {
    val userActor = webSocketSys.actorOf(Props(new ConnectedUser(chatRoom)))

    val incomingMessages: Sink[Message, NotUsed] =
      Flow[Message].map {
        case TextMessage.Strict(text) => MessageReceived(text)
      }.to(Sink.actorRef[MessageReceived](userActor, PoisonPill))

    val outgoingMessages: Source[Message, NotUsed] =
      Source.actorRef[MessagePosted](10, OverflowStrategy.fail)
          .mapMaterializedValue { outgoingActor =>
            userActor ! ConnectionEstablished(outgoingActor)
            NotUsed
          }
        .map(
          (outgoingMessage: MessagePosted) => TextMessage(outgoingMessage.text))

    Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
  }
}
