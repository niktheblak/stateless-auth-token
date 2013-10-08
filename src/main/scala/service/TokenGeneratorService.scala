package service

import spray.routing.HttpService
import akka.actor.ActorRef
import akka.pattern.ask
import tokens.Authentication
import java.util.{Calendar, Date}
import akka.util.Timeout
import service.TokenGeneratorActor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

trait TokenGeneratorService extends HttpService {
  implicit val context: ExecutionContext
  implicit val timeout: Timeout
  val tokenGeneratorActor: ActorRef
  
  val generateTokenRoute = path("token") {
    (get & parameters('userId.as[String], 'role.as[String])) {
      (userId, role) ⇒
        val auth = Authentication(userId, role, expireAfter(1.hours))
        val createTokenTask = ask(tokenGeneratorActor, CreateToken(auth)).mapTo[TokenCreated]
        val result = createTokenTask map { response ⇒
          response.token
        }
        complete(result)
    }
  }
  
  def expireAfter(duration: Duration): Date = {
    val c = Calendar.getInstance()
    c.add(Calendar.SECOND, duration.toSeconds.toInt)
    c.getTime
  }
}
