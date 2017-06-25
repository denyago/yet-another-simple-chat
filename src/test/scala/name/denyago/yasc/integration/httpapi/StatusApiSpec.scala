package name.denyago.yasc.integration.httpapi

import name.denyago.yasc.Main
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{Exceptional, FunSpec, Matchers, Outcome}

import scala.util.Try
import scalaj.http.Http

class StatusApiSpec extends FunSpec with Matchers with Eventually {
  override def withFixture(test: NoArgTest): Outcome = {
    val app = new Main(Array.empty[String])
    app.run()

    val outcome = Try { super.withFixture(test) }.recover {
      case t: Throwable => Exceptional(t)
    }.getOrElse(
      Exceptional(new RuntimeException("No test outcome present"))
    )

    app.stop()

    outcome
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
