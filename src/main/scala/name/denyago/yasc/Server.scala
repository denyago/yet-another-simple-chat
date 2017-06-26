package name.denyago.yasc

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import akka.stream.ActorMaterializer
import name.denyago.yasc.chat.ChatRoom
import name.denyago.yasc.config.{Factory => ConfigFactory}
import name.denyago.yasc.http.Service
import name.denyago.yasc.http.messages.{ChatRoomStarted, RetrySeverStart, ServerFailed, ServerStarted}

import scala.concurrent.duration._

/**
  * Overlooks for different internal services and orchestrates startup and shutdown.
  */
class Server extends Actor with ActorLogging {
  val maxHttpServiceTries = 3

  override def preStart(): Unit = {
    val chatRoom = context.actorOf(Props(classOf[ChatRoom]))
    self ! ChatRoomStarted(chatRoom)
  }

  override def receive: Receive = {
    case ChatRoomStarted(chatRoomRef) =>
      context.become(serverStarting(chatRoomRef, 0))
    case msg => log.error(s"Unexpected message $msg")
  }

  def serverStarting(chatRoom: ActorRef, httpServiceStartTry: Int): Receive = {
    val httpServer = new http.Service(context.system, ActorMaterializer(), ConfigFactory.httpServiceConf, chatRoom)

    if (httpServiceStartTry < maxHttpServiceTries) {
      httpServer.start(self)
    } else {
      chatRoom ! PoisonPill
      context.stop(self)
    }

    {
      case ServerStarted =>
        context.become(fullyInitialised(chatRoom, httpServer))
      case ServerFailed =>
        context.system
          .scheduler
          .scheduleOnce(2 seconds, self, RetrySeverStart(httpServiceStartTry + 1))(executor = context.dispatcher)
      case RetrySeverStart(tryCount) =>
        context.become(serverStarting(chatRoom, tryCount))
      case PoisonPill =>
        chatRoom ! PoisonPill
        context.stop(self)
      case msg => log.error(s"Unexpected message $msg")
    }
  }

  def fullyInitialised(chatRoom: ActorRef, httpServer: Service): Receive = {
    case PoisonPill =>
      httpServer.stop(self)
      chatRoom ! PoisonPill
      context.stop(self)
    case msg => log.error(s"Unexpected message $msg")
  }
}
