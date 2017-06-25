package name.denyago.yasc.integration.httpapi

import com.github.andyglow.websocket.WebsocketClient
import name.denyago.yasc.Main
import name.denyago.yasc.config.Factory
import org.scalatest._
import org.scalatest.concurrent.{Eventually, IntegrationPatience}

import scala.collection.mutable
import scala.util.Try
import scalaj.http.Http

class WebSocketSpec extends FunSpec with Matchers with Eventually with IntegrationPatience {
    val conf = Factory.httpServiceConf
    val url: String = s"${conf.host}:${conf.port}"

  override def withFixture(test: NoArgTest): Outcome = {
    val app = new Main(Array.empty[String])
    app.run()

    eventually {
      Http(s"http://$url/status").asString.code shouldEqual 200
    }

    val outcome = Try { super.withFixture(test) }.recover {
      case t: Throwable => Exceptional(t)
    }.getOrElse(
      Exceptional(new RuntimeException("No test outcome present"))
    )

    app.stop()

    outcome
  }

  describe("WebSocket API") {

    def mkClient(messages: mutable.MutableList[String]): WebsocketClient[String] =
      WebsocketClient[String](s"ws://$url/ws") {
          case str =>
            messages += str
        }

    describe("Posting a message") {
      it("should send it back to current user") {
        val messages = mutable.MutableList.empty[String]
        val client = mkClient(messages)

        val ws = client.open()

        ws ! "hello"
        ws ! "world"

        eventually { messages should contain allOf ("world", "hello") }
      }

      it("should send messages of a user to other users") {
        val userMessages = mutable.MutableList.empty[String]
        val otherUserMessages = mutable.MutableList.empty[String]

        val userClient = mkClient(userMessages)
        val otherUserClient = mkClient(otherUserMessages)

        val ws = userClient.open()
        otherUserClient.open()

        ws ! "Hello,"
        ws ! "my friend!"

        eventually { userMessages should contain allOf ("Hello,", "my friend!") }
        eventually { otherUserMessages should contain allOf ("Hello,", "my friend!") }
      }

      it("should send messages of a user to other users when they connect") {
        val userMessages = mutable.MutableList.empty[String]
        val otherUserMessages = mutable.MutableList.empty[String]
        val anotherUserMessages = mutable.MutableList.empty[String]

        val userClient = mkClient(userMessages)
        val otherUserClient = mkClient(otherUserMessages)
        val anotherUserClient = mkClient(anotherUserMessages)

        val userWs = userClient.open()

        userWs ! "one"

        eventually { userMessages should contain ("one") }

        val otherUserWs = otherUserClient.open()

        userWs ! "two"

        eventually { userMessages should contain allOf ("one", "two") }
        eventually { otherUserMessages should contain ("two") }

        otherUserWs.close()

        anotherUserClient.open()

        userWs ! "three"

        eventually { userMessages should contain allOf ("one", "two", "three") }
        eventually { otherUserMessages should contain ("two") }
        eventually { anotherUserMessages should contain ("three") }
      }
    }
  }
}
