package token

import java.security.SecureRandom

import crypto.Encryptor
import encoding.TokenEncoder

trait RandomSaltTokenCreator extends EncryptingTokenCreator { self: TokenEncoder with Encryptor ⇒
  val saltLength = 8
  val minimumTokenLength = saltLength + headerBytes.length + versionBytes.length
  val random = new SecureRandom

  override def generateSalt: Array[Byte] = {
    val salt = new Array[Byte](saltLength)
    random.nextBytes(salt)
    salt
  }
}
