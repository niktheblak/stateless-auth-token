package service

import akka.actor.{Status, Actor}
import tokens.{DefaultTokenCreator, AuthenticationException, Authentication}

class TokenGeneratorActor extends Actor with DefaultTokenCreator {
  import TokenGeneratorActor._

  val passPhrase = "^YS5Fe>8L@37E513U:69^6*UNY{?"

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
