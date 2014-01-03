package tokens

import base58.Base58
import java.security.SecureRandom

trait RandomSaltTokenCreator extends PayloadEncoder { self: TokenEncoder with Encrypter ⇒
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
    val payload = encodePayload(encodeToken(auth), passPhrase.toCharArray, salt)
    Base58.encode(salt ++ payload)
  }

  def decodeAuthToken(tokenString: String): Authentication = {
    val encrypted = Base58.decode(tokenString)
    checkLength(minimumTokenLength, encrypted.length, "Authentication token is too short")
    val salt = encrypted.slice(0, saltLength)
    val payload = encrypted.slice(saltLength, encrypted.length)
    val data = decodePayload(payload, passPhrase.toCharArray, salt)
    decodeToken(data)
  }
}
