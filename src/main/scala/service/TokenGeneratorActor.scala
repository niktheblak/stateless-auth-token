package service

import akka.actor.{ Actor, Status }
import auth.{ Authentication, AuthenticationException }
import token.DefaultTokenCreator

class TokenGeneratorActor extends Actor with DefaultTokenCreator {
  import TokenGeneratorActor._

  override val password: Array[Char] = "^YS5Fe>8L@37E513U:69^6*UNY{?".toCharArray

  def receive = {
    case CreateToken(auth) ⇒
      val token = createTokenString(auth)
      sender ! TokenCreated(token)
    case DecodeToken(token) ⇒
      try {
        val auth = readTokenString(token)
        sender ! TokenDecoded(auth)
      } catch {
        case e: AuthenticationException ⇒
          sender ! Status.Failure(e)
      }
  }
}

object TokenGeneratorActor {
  case class CreateToken(auth: Authentication)
  case class TokenCreated(token: String)
  case class DecodeToken(token: String)
  case class TokenDecoded(auth: Authentication)
}
