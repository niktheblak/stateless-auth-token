package service

import spray.routing.SimpleRoutingApp
import akka.util.Timeout
import akka.actor.{Props, ActorSystem}
import akka.routing.SmallestMailboxRouter
import scala.concurrent.duration._
import scala.language.postfixOps

object SprayBootstrap extends App with SimpleRoutingApp with TokenGeneratorService {
  implicit val timeout: Timeout = 30 seconds
  implicit val system = ActorSystem("token-generator")
  implicit val context = system.dispatcher
  val processors = Runtime.getRuntime.availableProcessors()
  val tokenGeneratorActor = system.actorOf(Props[TokenGeneratorActor].withRouter(SmallestMailboxRouter(processors)))

  startServer(interface = "localhost", port = 8080)(routes)
}
