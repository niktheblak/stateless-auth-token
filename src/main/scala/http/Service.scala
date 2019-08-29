package http

import java.time.Instant

import auth.{ Authentication, Roles }
import org.scalatra._
import token.DefaultTokenCreator

class Service extends ScalatraServlet with DefaultTokenCreator {
  get("/token") {
    val userId = params.get("user_id") match {
      case Some(uid) => uid
      case None => halt(400, "Missing user_id")
    }
    val role = (for {
      p <- params.get("role");
      r <- Roles.parse(p)
    } yield r).getOrElse(Roles.User)
    val now = Instant.now()
    createTokenString(Authentication(userId, role, now.plusSeconds(3600)))
  }

  get("/auth") {
    val token = request.header("Authorization") match {
      case Some(auth) => Some(auth.replace("Bearer", "").trim)
      case None => params.get("token")
    }
    token match {
      case Some(t) =>
        val now = Instant.now()
        val auth = readTokenString(t)
        assert(auth.expirationTime.isAfter(now))
        auth.toString
      case None => halt(400, "Missing token")
    }
  }

  error {
    case e: Throwable =>
      log("Failed to create/decode token", e)
      e.getMessage
  }
}
