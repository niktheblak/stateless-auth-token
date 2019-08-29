package token

import java.time.Instant

import auth.{ Authentication, Roles }
import org.scalatest.{ FlatSpec, Matchers }

object TestDefaultTokenCreator extends DefaultTokenCreator {}

class DefaultTokenCreatorTest extends FlatSpec with Matchers {
  import TestDefaultTokenCreator._

  "tokens.DefaultTokenCreator" should "encode and decode token" in {
    val now = Instant.now()
    val auth = Authentication("testUser", Roles.User, now.plusSeconds(3600))
    val token = createTokenString(auth)
    val decoded = readTokenString(token)
    decoded.userId should equal(auth.userId)
    decoded.role should equal(auth.role)
    decoded.expirationTime should equal(auth.expirationTime)
    decoded should equal(auth)
  }
}
