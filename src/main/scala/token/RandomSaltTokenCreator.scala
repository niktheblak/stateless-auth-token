package token

import java.security.SecureRandom

import auth.Authentication
import crypto.Encryptor
import encoding.{Base58StringEncoder, PayloadEncoder, TokenEncoder}

trait RandomSaltTokenCreator extends PayloadEncoder with Base58StringEncoder { self: TokenEncoder with Encryptor ⇒
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
    val salt = generateSalt
    val payload = encodePayload(encodeToken(auth))
    val encrypted = encrypt(payload, passPhrase.toCharArray, salt)
    encodeString(salt ++ encrypted)
  }

  def decodeAuthToken(tokenString: String): Authentication = {
    val encrypted = decodeString(tokenString)
    checkLength(minimumTokenLength, encrypted.length, "Authentication token is too short")
    val salt = encrypted.slice(0, saltLength)
    val encryptedData = encrypted.slice(saltLength, encrypted.length)
    val data = decrypt(encryptedData, passPhrase.toCharArray, salt)
    val tokenData = decodePayload(data)
    decodeToken(tokenData)
  }

  def generateSalt: Array[Byte] = {
    val salt = new Array[Byte](saltLength)
    random.nextBytes(salt)
    salt
  }
}
