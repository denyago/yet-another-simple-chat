package name.denyago.yasc.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.Message
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Flow

/**
  * Describes all HTTP API routes
  */
trait Router {
  def wsFlow: Flow[Message, Message, Any]

  val routes: Route =
    path("status") {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, "{\"status\":\"OK\"}"))
      }
    } ~
      path("ws")(handleWebSocketMessages(wsFlow))
}
