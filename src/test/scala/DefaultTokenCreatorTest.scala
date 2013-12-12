import java.util.Calendar
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import tokens.Authentication

class DefaultTokenCreatorTest extends FlatSpec with Matchers {
  "DefaultTokenCreator" should "encode and decode token" in {
    val expTime = Calendar.getInstance()
    expTime.add(Calendar.HOUR_OF_DAY, 1)
    val auth = Authentication("testUser", "testRole", expTime.getTime)
    val token = DefaultTokenCreator.createAuthToken(auth)
    println("Authentication token length: " + token.length)
    val decoded = DefaultTokenCreator.decodeAuthToken(token)
    decoded.userId should equal(auth.userId)
    decoded.role should equal(auth.role)
    decoded.expirationTime should equal(auth.expirationTime)
    decoded should equal(auth)
  }
}
