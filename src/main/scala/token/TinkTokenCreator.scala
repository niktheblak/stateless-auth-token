package token

import java.security.MessageDigest

import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.subtle.AesGcmJce
import crypto.Encryptor
import encoding.TokenEncoder

trait TinkTokenCreator extends EncryptingTokenCreator with Encryptor { self: TokenEncoder =>
  val password: String

  lazy val encryptor: Aead = {
    AeadConfig.register()
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(password.getBytes("UTF-8"))
    val key256Bit = messageDigest.digest()
    val key128Bit = key256Bit.take(16)
    new AesGcmJce(key128Bit)
  }

  override def encrypt(data: Array[Byte]): Array[Byte] = {
    encryptor.encrypt(data, Array.empty[Byte])
  }

  override def decrypt(encrypted: Array[Byte]): Array[Byte] = {
    encryptor.decrypt(encrypted, Array.empty[Byte])
  }
}
