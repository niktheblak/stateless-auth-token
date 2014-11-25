package service

import spray.testkit.ScalatestRouteTest
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import akka.actor.{ ActorSystem, Props }

class TokenGeneratorServiceTest extends FlatSpec with TokenGeneratorService with Matchers with ScalatestRouteTest {
  implicit def actorRefFactory: ActorSystem = system
  implicit val context = system.dispatcher
  implicit val timeout = Timeout(5, TimeUnit.SECONDS)
  val tokenGeneratorActor = actorRefFactory.actorOf(Props[TokenGeneratorActor])

  "TokenGeneratorService" should "generate a non-empty token" in {
    Get("/token?userId=x&role=y") ~> generateTokenRoute ~> check {
      responseAs[String] should not be 'empty
    }
  }
}
