package crypto

import java.security.SecureRandom

import org.jasypt.salt.SaltGenerator

trait RandomSaltGenerator extends SaltGenerator {
  val random = new SecureRandom

  override def includePlainSaltInEncryptionResults(): Boolean = true

  override def generateSalt(lengthBytes: Int): Array[Byte] = {
    val salt = new Array[Byte](lengthBytes)
    random.nextBytes(salt)
    salt
  }
}
