package service

import spray.routing.SimpleRoutingApp
import akka.util.Timeout
import akka.actor.{ Props, ActorSystem }
import akka.routing.SmallestMailboxPool
import scala.concurrent.duration._

object SprayBootstrap extends App with SimpleRoutingApp with TokenGeneratorService {
  implicit val timeout: Timeout = 30.seconds
  implicit val system = ActorSystem("token-generator")
  implicit val context = system.dispatcher
  val processors = Runtime.getRuntime.availableProcessors()
  val tokenGeneratorActor = system.actorOf(Props[TokenGeneratorActor].withRouter(SmallestMailboxPool(processors)))

  startServer(interface = "localhost", port = 8080)(routes)
}
