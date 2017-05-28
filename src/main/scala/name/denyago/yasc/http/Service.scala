package name.denyago.yasc.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import name.denyago.yasc.config.HttpServiceConf
import name.denyago.yasc.log.{Helper => LogHelper}

/**
  * HTTP API Endpoint
  */
class Service(sys: ActorSystem, mat: ActorMaterializer, config: HttpServiceConf) extends Router with LogHelper {
  implicit val executionContext = sys.dispatcher
  implicit val system = sys
  implicit val materializer = mat

  val host = config.host
  val port = config.port

  lazy val bindingFuture = Http().bindAndHandle(routes, host, port)

  def stop() = bindingFuture.flatMap(_.unbind()).onComplete(_ => log.info("HTTP API Service stopped"))

  def start() = bindingFuture.map(_ => log.info(s"HTTP API Service started at http://$host:$port"))

}
