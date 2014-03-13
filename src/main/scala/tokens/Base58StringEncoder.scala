package tokens

import base58.Base58

trait Base58StringEncoder extends StringEncoder {
  def encodeString(data: Array[Byte]): String =
    Base58.encode(data)

  def decodeString(data: String): Array[Byte] =
    Base58.decode(data)
}