package tokens

import base58.Base58

trait Base58StringEncoder extends StringEncoder {
  override def encodeString(data: Array[Byte]): String =
    Base58.encode(data)

  override def decodeString(data: String): Array[Byte] =
    Base58.decode(data)
}
