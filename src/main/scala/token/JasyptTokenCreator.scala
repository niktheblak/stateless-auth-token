package token

import crypto.Encryptor
import encoding.TokenEncoder
import org.jasypt.util.binary.{BasicBinaryEncryptor, BinaryEncryptor}

trait JasyptTokenCreator extends EncryptingTokenCreator with Encryptor { self: TokenEncoder ⇒
  val password: Array[Char]

  lazy val encryptor: BinaryEncryptor = {
    val encryptor = new BasicBinaryEncryptor
    encryptor.setPasswordCharArray(password)
    encryptor
  }

  override def encrypt(data: Array[Byte], salt: Array[Byte]): Array[Byte] = {
    encryptor.encrypt(data)
  }

  override def decrypt(encrypted: Array[Byte], salt: Array[Byte]): Array[Byte] = {
    encryptor.decrypt(encrypted)
  }
}
