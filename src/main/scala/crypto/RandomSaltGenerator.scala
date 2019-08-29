package crypto

import java.security.SecureRandom

trait RandomSaltGenerator {
  val random = new SecureRandom

  def generateSalt(lengthBytes: Int): Array[Byte] = {
    val salt = new Array[Byte](lengthBytes)
    random.nextBytes(salt)
    salt
  }
}
