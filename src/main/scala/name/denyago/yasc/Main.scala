package name.denyago.yasc

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer

import log.{ Helper => LogHelper }
import scala.concurrent.{Await, TimeoutException}
import scala.util.Try

import scala.concurrent.duration._

/**
  * Main class to launch the chat server application.
  */
class Main(args: Array[String]) extends LogHelper {

  implicit val system = ActorSystem("yasc-server")
  implicit val materializer = ActorMaterializer()

  val any = List(1, true, "three")

  def run(): Unit = {
    log.info(s"Statring the App: Starting the Chat Server...")
    system.actorOf(Props(classOf[Server]), "main-server")
  }

  def stop(): Unit = {
    val timeout = (20 seconds)
    log.info(s"Stopping the App: Asking Akka System to terminate. Waiting for $timeout")

    Try(Await.result(system.terminate(), timeout)).
      map(_ => log.info("Stopping the App: Stopped gracefully.")).
      recover {
        case _: TimeoutException => log.error("Stopping the App: it took too long. The app is killed :(")
        case throwable: Throwable => log.error(s"Stopping the App: Exception happened - ${throwable.getMessage}")
      }
  }
}

object Main extends App {
  val app = new Main(args)
  app.run()

  sys.addShutdownHook({
    app.stop()
  })
}


