package token

import crypto.Encryptor
import encoding.TokenEncoder

trait TinkTokenCreator extends EncryptingTokenCreator with Encryptor { self: TokenEncoder with AeadProvider =>
  override def encrypt(data: Array[Byte]): Array[Byte] = {
    aead.encrypt(data, Array.empty[Byte])
  }

  override def decrypt(encrypted: Array[Byte]): Array[Byte] = {
    aead.decrypt(encrypted, Array.empty[Byte])
  }
}
