package token

import crypto.Encryptor
import encoding.TokenEncoder
import org.jasypt.encryption.pbe.{ PBEByteCleanablePasswordEncryptor, StandardPBEByteEncryptor }
import org.jasypt.salt.SaltGenerator

trait JasyptTokenCreator extends EncryptingTokenCreator with Encryptor { self: TokenEncoder ⇒
  val password: Array[Char]

  lazy val encryptor: PBEByteCleanablePasswordEncryptor = {
    val encryptor = new StandardPBEByteEncryptor
    encryptor.setAlgorithm("PBEWithMD5AndDES")
    encryptor.setPasswordCharArray(password)
    this match {
      case gen: SaltGenerator ⇒
        encryptor.setSaltGenerator(gen)
      case _ ⇒
    }
    encryptor.initialize()
    encryptor
  }

  override def encrypt(data: Array[Byte]): Array[Byte] = {
    encryptor.encrypt(data)
  }

  override def decrypt(encrypted: Array[Byte]): Array[Byte] = {
    encryptor.decrypt(encrypted)
  }
}
