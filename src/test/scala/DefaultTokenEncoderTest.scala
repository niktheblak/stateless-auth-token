import DefaultTokenEncoder._
import java.util.Calendar
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import tokens.Authentication

class DefaultTokenEncoderTest extends FlatSpec with ShouldMatchers {
  "DefaultTokenEncoder" should "encode and decode token" in {
    val expTime = Calendar.getInstance()
    expTime.add(Calendar.HOUR_OF_DAY, 1)
    val auth = Authentication("testUser", "testRole", expTime.getTimeInMillis())
    val token = createAuthToken(auth)
    val decoded = decodeAuthToken(token)
    decoded should equal(auth)
  }
}
