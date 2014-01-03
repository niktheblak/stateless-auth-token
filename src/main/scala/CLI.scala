import tokens._
import java.util.Calendar

object CLI extends App with DefaultTokenCreator {
  val passPhrase = "^YS5Fe>8L@37E513U:69^6*UNY{?"
  val expTime = Calendar.getInstance()
  expTime.add(Calendar.HOUR_OF_DAY, 1)
  val token = createAuthToken(Authentication("testUser", "testRole", expTime.getTime))
  println(s"Token length: ${token.size} bytes")
  println(s"Token: $token")
  val decoded = decodeAuthToken(token)
  println(decoded)
}