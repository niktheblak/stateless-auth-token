package token

import java.util.Calendar

import auth.Authentication
import org.scalatest.{FlatSpec, Matchers}

object TestDefaultTokenCreator extends DefaultTokenCreator {
  override val password: Array[Char] = "testPassPhrase".toCharArray

  override def generateSalt(lengthBytes: Int): Array[Byte] =
    new Array[Byte](lengthBytes)
}

class DefaultTokenCreatorTest extends FlatSpec with Matchers {
  import TestDefaultTokenCreator._

  "tokens.DefaultTokenCreator" should "encode and decode token" in {
    val expTime = Calendar.getInstance()
    expTime.add(Calendar.HOUR_OF_DAY, 1)
    val auth = Authentication("testUser", "testRole", expTime.getTime)
    val token = createTokenString(auth)
    val decoded = readTokenString(token)
    decoded.userId should equal(auth.userId)
    decoded.role should equal(auth.role)
    decoded.expirationTime should equal(auth.expirationTime)
    decoded should equal(auth)
  }
}
