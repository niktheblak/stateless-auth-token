package service

import tokens.{Authentication, AuthenticationException}
import service.TokenGeneratorActor._
import spray.http.{HttpResponse, StatusCodes}
import spray.routing.HttpService
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import java.util.{Calendar, Date}
import scala.concurrent.duration._
import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Try, Success, Failure}

trait TokenGeneratorService extends HttpService {
  implicit val context: ExecutionContext
  implicit val timeout: Timeout
  val tokenGeneratorActor: ActorRef
  
  val generateTokenRoute = path("token") {
    (get & parameters('userId.as[String], 'role.as[String])) {
      (userId, role) ⇒
        val auth = Authentication(userId, role, expireAfter(1.hours))
        val createTokenTask = ask(tokenGeneratorActor, CreateToken(auth)).mapTo[TokenCreated]
        complete(createTokenTask.map(_.token))
    }
  }

  val authRoute = path("auth") {
    (get & parameters('token.as[String])) { token ⇒
      val authTask = authenticate(token)
      complete(authTask map {
        case Success(auth) => HttpResponse(entity = "Welcome, " + auth.userId)
        case Failure(e) => HttpResponse(status = StatusCodes.Unauthorized)
      })
    }
  }
  
  def authenticate(token: String): Future[Try[Authentication]] = {
    val authTask = ask(tokenGeneratorActor, DecodeToken(token)).mapTo[TokenDecoded]
    authTask map { response =>
      Success(response.auth)
    } recover {
      case e: AuthenticationException => Failure(e)
    }
  }

  val routes = generateTokenRoute ~ authRoute
  
  def expireAfter(duration: Duration): Date = {
    val c = Calendar.getInstance()
    c.add(Calendar.SECOND, duration.toSeconds.toInt)
    c.getTime
  }
}
