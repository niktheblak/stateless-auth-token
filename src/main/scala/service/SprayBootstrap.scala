package service

import spray.routing.SimpleRoutingApp
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import akka.actor.{Props, ActorSystem}
import akka.routing.SmallestMailboxRouter

object SprayBootstrap extends App with SimpleRoutingApp with TokenGeneratorService {
  implicit val timeout = Timeout(30, TimeUnit.SECONDS)
  implicit val system = ActorSystem("token-generator")
  implicit val context = system.dispatcher
  val processors = Runtime.getRuntime.availableProcessors()
  val tokenGeneratorActor = system.actorOf(Props[TokenGeneratorActor].withRouter(SmallestMailboxRouter(processors)))

  startServer(interface = "localhost", port = 8080)(generateTokenRoute)
}
