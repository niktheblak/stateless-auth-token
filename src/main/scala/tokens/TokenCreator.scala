package tokens

import base58.Base58
import serialization.InvalidDataException

trait TokenCreator { self: TokenEncoder with Encrypter ⇒
  val header = "AUTH-TOKEN"
  val version = "1.0"
  val encodingCharset = "UTF-8"
  val headerBytes = header.getBytes(encodingCharset)
  val versionBytes = version.getBytes(encodingCharset)

  def passPhrase: String
  def salt: Array[Byte]
    
  def createAuthToken(auth: Authentication): String = {
    val tokenData = headerBytes ++ versionBytes ++ encodeToken(auth)
    val encrypted = encrypt(tokenData, passPhrase.toCharArray, salt)
    Base58.encode(encrypted)
  }
  
  def decodeAuthToken(tokenString: String): Authentication = {
    val encrypted = Base58.decode(tokenString)
    val decrypted = decrypt(encrypted, passPhrase.toCharArray, salt)
    val header = decrypted.slice(0, headerBytes.length)
    checkData(headerBytes, header, "Authentication token header not found")
    val versionStart = headerBytes.size
    val versionEnd = versionStart + versionBytes.size
    val version = decrypted.slice(versionStart, versionEnd)
    checkData(versionBytes, version, "Unsupported version " + new String(version, encodingCharset))
    decodeToken(decrypted.slice(versionEnd, decrypted.size))
  }

  def checkData(expected: Array[Byte], actual: Array[Byte], message: String) {
    if (!java.util.Arrays.equals(expected, actual)) {
      throw new InvalidDataException(message)
    }
  }
}