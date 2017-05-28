package name.denyago.yasc.httpapi

import name.denyago.yasc.Main
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import scalaj.http.Http

class StatusApiSpec extends FunSpec with Matchers with Eventually with BeforeAndAfterEach {
  var app: Main = null

  override def beforeEach(): Unit = {
    app = new Main(Array.empty[String])
    app.run()
    super.beforeEach()
  }

  override def afterEach(): Unit = {
    app.stop()
    super.afterEach()
  }

  describe("Status HTTP API") {

    implicit val patienceConfig =
      PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(100, Millis)))

    it("should return OK status when the server up and running") {
      eventually {
        val response = Http("http://localhost:8888/status").asString

        response.code shouldEqual 200
        response.body shouldEqual "{\"status\":\"OK\"}"
      }
    }
  }
}
