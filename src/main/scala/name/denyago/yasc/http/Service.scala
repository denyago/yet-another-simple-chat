package name.denyago.yasc.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import name.denyago.yasc.log.{Helper => LogHelper}

/**
  * HTTP API Endpoint
  */
class Service(sys: ActorSystem, mat: ActorMaterializer) extends Router with LogHelper {
  implicit val executionContext = sys.dispatcher
  implicit val system = sys
  implicit val materializer = mat

  lazy val bindingFuture = Http().bindAndHandle(routes, "localhost", 8888)

  def stop() = bindingFuture.flatMap(_.unbind()).onComplete(_ => log.info("HTTP API Service stopped"))

  def start() = bindingFuture.map(_ => log.info("HTTP API Service started"))

}
