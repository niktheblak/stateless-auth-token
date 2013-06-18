import tokens._
import DefaultTokenCreator._
import java.util.Calendar

object CLI extends App {
  println("Salt: " + new String(DefaultTokenCreator.salt, "US-ASCII"))
  val expTime = Calendar.getInstance()
  expTime.add(Calendar.HOUR_OF_DAY, 1)
  val token = createAuthToken(Authentication("testUser", "testRole", expTime.getTime))
  println(token)
  val decoded = decodeAuthToken(token)
  println(decoded)
}