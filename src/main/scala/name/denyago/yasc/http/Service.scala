package name.denyago.yasc.http

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import name.denyago.yasc.config.HttpServiceConf
import name.denyago.yasc.http.messages.{ServerFailed, ServerStarted, ServerStopped}
import name.denyago.yasc.http.websocket.WsFlow
import name.denyago.yasc.log.{Helper => LogHelper}

/**
  * HTTP API Endpoint
  */
class Service(sys: ActorSystem, mat: ActorMaterializer, config: HttpServiceConf, chatRoomRef: ActorRef)
  extends Router
    with LogHelper
    with WsFlow {
  implicit val executionContext = sys.dispatcher
  implicit val system = sys
  implicit val materializer = mat
  val chatRoom = chatRoomRef

  val host = config.host
  val port = config.port

  lazy val bindingFuture = Http().bindAndHandle(routes, host, port)

  def stop(supervisor: ActorRef) =
    bindingFuture
      .flatMap(_.unbind())
      .onComplete { _ =>
        supervisor ! ServerStopped
        log.info("HTTP API Service stopped")
      }

  def start(supervisor: ActorRef) =
    bindingFuture.map { _ =>
      supervisor ! ServerStarted
      log.info(s"HTTP API Service started at http://$host:$port")
    }.recover{
      case t: Throwable =>
        supervisor ! ServerFailed
        log.error(s"HTTP API Service failed to start: $t")
    }

}
