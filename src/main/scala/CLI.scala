import java.util.Calendar

import auth.Authentication
import token._

object CLI extends App with DefaultTokenCreator {
  override def passPhrase = "^YS5Fe>8L@37E513U:69^6*UNY{?"
  val expTime = Calendar.getInstance()
  expTime.add(Calendar.HOUR_OF_DAY, 1)
  val token = createTokenString(Authentication("testUser", "testRole", expTime.getTime))
  println(s"Token length: ${token.length} bytes")
  println(s"Token: $token")
  val decoded = readTokenString(token)
  println(decoded)
}