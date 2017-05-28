package name.denyago.yasc.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * Describes all HTTP API routes
  */
trait Router {
  val routes: Route =
    path("status") {
      get {
        complete(HttpEntity(ContentTypes.`application/json`, "{\"status\":\"OK\"}"))
      }
    }
}
