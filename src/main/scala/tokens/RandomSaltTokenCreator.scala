package tokens

import base58.Base58
import java.security.SecureRandom
import serialization.InvalidDataException

trait RandomSaltTokenCreator { self: TokenEncoder with Encrypter ⇒
  val header = "AUTH"
  val version = 2
  val encodingCharset = "UTF-8"
  val headerBytes = header.getBytes(encodingCharset)
  val versionBytes = Array[Byte](version.toByte)
  val saltLength = 8
  val minimumTokenLength = saltLength + headerBytes.length + versionBytes.length
  val random = new SecureRandom

  def passPhrase: String

  def createAuthToken(auth: Authentication): String = {
    val salt = new Array[Byte](saltLength)
    random.nextBytes(salt)
    val tokenData = headerBytes ++ versionBytes ++ encodeToken(auth)
    val encrypted = encrypt(tokenData, passPhrase.toCharArray, salt)
    Base58.encode(salt ++ encrypted)
  }

  def decodeAuthToken(tokenString: String): Authentication = {
    val encrypted = Base58.decode(tokenString)
    checkLength(minimumTokenLength, encrypted.length, "Authentication token is too short")
    val salt = encrypted.slice(0, saltLength)
    val payload = encrypted.slice(saltLength, encrypted.length)
    val decrypted = decrypt(payload, passPhrase.toCharArray, salt)
    val header = decrypted.slice(0, headerBytes.length)
    checkData(headerBytes, header, "Authentication token header not found")
    val versionStart = headerBytes.size
    val versionEnd = versionStart + versionBytes.size
    val version = decrypted.slice(versionStart, versionEnd)
    checkData(versionBytes, version, "Unsupported version " + new String(version, encodingCharset))
    decodeToken(decrypted.slice(versionEnd, decrypted.size))
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
