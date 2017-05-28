package name.denyago.yasc

import akka.actor.{Actor, ActorLogging}
import akka.stream.ActorMaterializer
import config.{ Factory => ConfigFactory }

/**
  * Overlooks for different internal services and orchestrates startup and shutdown.
  */
class Server extends Actor with ActorLogging {

  lazy val httpServer = new http.Service(context.system, ActorMaterializer(), ConfigFactory.httpServiceConf)

  override def preStart(): Unit = {
    httpServer.start()
    super.preStart()
  }

  override def postStop(): Unit = {
    httpServer.stop()
    super.postStop()
  }

  override def receive: Receive = {
    case msg => log.error(s"Unexpected message $msg")
  }
}
