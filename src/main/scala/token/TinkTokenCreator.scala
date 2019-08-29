package token

import com.google.crypto.tink.aead.{ AeadConfig, AeadKeyTemplates }
import com.google.crypto.tink.{ Aead, KeysetHandle }
import crypto.Encryptor
import encoding.TokenEncoder

trait TinkTokenCreator extends EncryptingTokenCreator with Encryptor { self: TokenEncoder =>
  lazy val encryptor: Aead = {
    AeadConfig.register()
    val keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM)
    keysetHandle.getPrimitive(classOf[Aead])
  }

  override def encrypt(data: Array[Byte]): Array[Byte] = {
    encryptor.encrypt(data, Array.empty[Byte])
  }

  override def decrypt(encrypted: Array[Byte]): Array[Byte] = {
    encryptor.decrypt(encrypted, Array.empty[Byte])
  }
}
