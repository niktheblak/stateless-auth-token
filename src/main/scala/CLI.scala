import java.util.Calendar

import auth.{ Authentication, Roles }
import token._

object CLI extends App with DefaultTokenCreator {
  val expTime = Calendar.getInstance()
  expTime.add(Calendar.HOUR_OF_DAY, 1)
  val token = createTokenString(Authentication("testUser", Roles.User, expTime.getTime))
  println(s"Token length: ${token.length} bytes")
  println(s"Token: $token")
  val decoded = readTokenString(token)
  println(decoded)
}