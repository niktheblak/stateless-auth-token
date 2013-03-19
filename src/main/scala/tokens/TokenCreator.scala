package tokens

import base58.Base58
import java.util.Arrays
import serialization.InvalidDataException

trait TokenCreator extends FieldEncoderTokenEncoder with AESSharedKeyEncrypter {
  val header = "AUTH-TOKEN"
  val version = "1.0"
  
  val encodingCharset: String
  def passPhrase: String
  def salt: Array[Byte]
  
  def headerBytes = header.getBytes(encodingCharset)
  def versionBytes = version.getBytes(encodingCharset)
    
  def createAuthToken(auth: Authentication): String = {
    val tokenData = encodeToken(auth)
    val payload = headerBytes ++ versionBytes ++ tokenData
    val encrypted = encrypt(payload, passPhrase.toCharArray, salt)
    Base58.encode(encrypted)
  }
  
  def decodeAuthToken(tokenString: String): Authentication = {
    val encryptedTokenData = Base58.decode(tokenString)
    val decryptedTokenData = decrypt(encryptedTokenData, passPhrase.toCharArray, salt)
    val header = decryptedTokenData.slice(0, headerBytes.length)
    checkData(headerBytes, header, "Authentication token header not found")
    val version = decryptedTokenData.slice(headerBytes.length, headerBytes.length + versionBytes.length)
    checkData(versionBytes, version, "Unsupported version " + new String(version, encodingCharset))
    val payloadStart = headerBytes.length + versionBytes.length
    val tokenData = decryptedTokenData.slice(payloadStart, decryptedTokenData.length)
    decodeToken(tokenData)
  }

  def checkData(expected: Array[Byte], actual: Array[Byte], message: String) {
    if (!Arrays.equals(expected, actual)) {
      throw new InvalidDataException(message);
    }
  }
}