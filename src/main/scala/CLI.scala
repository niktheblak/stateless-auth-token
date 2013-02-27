import tokens._
import DefaultTokenEncoder._
import java.util.Calendar

object CLI extends App {
  val expTime = Calendar.getInstance()
  expTime.add(Calendar.HOUR_OF_DAY, 1)
  val token = createAuthToken(Authentication("testUser", "testRole", expTime.getTime))
  println(token)
  val decoded = decodeAuthToken(token)
  println(decoded)
}