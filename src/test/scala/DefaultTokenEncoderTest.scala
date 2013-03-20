import java.util.Calendar
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import tokens.Authentication

class DefaultTokenEncoderTest extends FlatSpec with ShouldMatchers {
  "DefaultTokenEncoder" should "encode and decode token" in {
    println("Salt: " + new String(DefaultTokenEncoder.salt, "US-ASCII"))
    val expTime = Calendar.getInstance()
    expTime.add(Calendar.HOUR_OF_DAY, 1)
    val auth = Authentication("testUser", "testRole", expTime.getTime)
    val token = DefaultTokenEncoder.createAuthToken(auth)
    println("Authentication token length: " + token.length)
    val decoded = DefaultTokenEncoder.decodeAuthToken(token)
    decoded should equal(auth)
  }
}
