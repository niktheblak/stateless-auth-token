package service

import java.util.Date

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import auth.{Authentication, AuthenticationException}
import service.TokenGeneratorActor._
import spray.http.{HttpResponse, StatusCodes}
import spray.routing.HttpService

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait TokenGeneratorService extends HttpService {
  implicit val context: ExecutionContext
  implicit val timeout: Timeout
  val tokenGeneratorActor: ActorRef

  val generateTokenRoute = path("token") {
    get {
      parameters('userId.as[String], 'role.as[String]) { (userId, role) ⇒
        complete {
          val auth = Authentication(userId, role, expireAfter(1.hours))
          val createTokenTask = ask(tokenGeneratorActor, CreateToken(auth)).mapTo[TokenCreated]
          createTokenTask.map(_.token)
        }
      }
    }
  }

  val authRoute = path("auth") {
    (get | post) {
      parameter('token.as[String]) { token ⇒
        complete {
          authenticate(token) map {
            case Success(auth) ⇒ HttpResponse(entity = "Welcome, " + auth.userId)
            case Failure(e) ⇒ HttpResponse(status = StatusCodes.Unauthorized, entity = e.getMessage)
          }
        }
      }
    }
  }

  def authenticate(token: String): Future[Try[Authentication]] = {
    val authTask = ask(tokenGeneratorActor, DecodeToken(token)).mapTo[TokenDecoded]
    authTask map { response ⇒
      if (response.auth.expirationTime.after(new Date)) Success(response.auth)
      else Failure(new AuthenticationException("Token expired on " + response.auth.expirationTime))
    } recover {
      case e: AuthenticationException ⇒ Failure(e)
    }
  }

  val routes = generateTokenRoute ~ authRoute

  def expireAfter(duration: FiniteDuration): Date =
    new Date(System.currentTimeMillis + duration.toMillis)
}
