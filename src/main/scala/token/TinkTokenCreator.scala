package token

import com.google.crypto.tink.Aead
import com.google.crypto.tink.aead.AeadConfig
import crypto.Encryptor
import encoding.TokenEncoder

trait TinkTokenCreator extends EncryptingTokenCreator with Encryptor { self: TokenEncoder with AeadProvider =>
  lazy val encryptor: Aead = {
    AeadConfig.register()
    getAead
  }

  override def encrypt(data: Array[Byte]): Array[Byte] = {
    encryptor.encrypt(data, Array.empty[Byte])
  }

  override def decrypt(encrypted: Array[Byte]): Array[Byte] = {
    encryptor.decrypt(encrypted, Array.empty[Byte])
  }
}
