package tokens

import serialization.InvalidDataException

trait PayloadEncoder { self: TokenEncoder with Encrypter ⇒
  val headerBytes: Array[Byte]
  val versionBytes: Array[Byte]
  val encodingCharset: String
  
  def encodePayload(data: Array[Byte], passPhrase: Array[Char], salt: Array[Byte]): Array[Byte] = {
    val tokenData = headerBytes ++ versionBytes ++ data
    encrypt(tokenData, passPhrase, salt)
  }
  
  def decodePayload(payload: Array[Byte], passPhrase: Array[Char], salt: Array[Byte]): Array[Byte] = {
    val decrypted = decrypt(payload, passPhrase, salt)
    val header = decrypted.slice(0, headerBytes.length)
    checkData(headerBytes, header, "Authentication token header not found")
    val versionStart = headerBytes.size
    val versionEnd = versionStart + versionBytes.size
    val version = decrypted.slice(versionStart, versionEnd)
    checkData(versionBytes, version, "Unsupported version " + new String(version, encodingCharset))
    decrypted.slice(versionEnd, decrypted.size)
  }

  def checkLength(expectedLength: Int, actualLength: Int, message: String) {
    if (actualLength < expectedLength) {
      throw new InvalidDataException(message)
    }
  }

  def checkData(expected: Array[Byte], actual: Array[Byte], message: String) {
    if (!java.util.Arrays.equals(expected, actual)) {
      throw new InvalidDataException(message)
    }
  }
}
