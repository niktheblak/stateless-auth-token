package service

import akka.actor.{Status, Actor}
import tokens.{AuthenticationException, Authentication}

class TokenGeneratorActor extends Actor with HostNameTokenCreator {
  import TokenGeneratorActor._

  def receive = {
    case CreateToken(auth) ⇒
      val token = createAuthToken(auth)
      sender ! TokenCreated(token)
    case DecodeToken(token) ⇒
      try {
        val auth = decodeAuthToken(token)
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
