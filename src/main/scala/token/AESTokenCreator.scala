package token

import crypto.{ AESSharedKeyEncryptor, Encryptor }
import encoding.TokenEncoder
import org.jasypt.salt.SaltGenerator

trait AESTokenCreator extends EncryptingTokenCreator with AESSharedKeyEncryptor with Encryptor with SaltGenerator { self: TokenEncoder =>
  val saltLength = 8
  val password: Array[Char]

  override def encrypt(data: Array[Byte]): Array[Byte] = {
    val salt = generateSalt(saltLength)
    salt ++ encrypt(data, password, salt)
  }

  override def decrypt(encrypted: Array[Byte]): Array[Byte] = {
    require(encrypted.length > saltLength)
    val salt = encrypted.slice(0, saltLength)
    decrypt(encrypted.drop(saltLength), password, salt)
  }
}
