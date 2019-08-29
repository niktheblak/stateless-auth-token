import java.time.Instant

import auth.{ Authentication, Roles }
import token._

object CLI extends App with DefaultTokenCreator {
  val now = Instant.now()
  val token = createTokenString(Authentication("testUser", Roles.User, now.plusSeconds(3600)))
  println(s"Token length: ${token.length} bytes")
  println(s"Token: $token")
  val decoded = readTokenString(token)
  println(decoded)
}