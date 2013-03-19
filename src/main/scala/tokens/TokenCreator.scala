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
    val encrypted = encrypt(payload, salt)
    Base58.encode(encrypted)
  }
  
  def decodeAuthToken(tokenString: String): Authentication = {
    val encryptedTokenData = Base58.decode(tokenString)
    val decryptedTokenData = decrypt(encryptedTokenData, salt)
    val header = decryptedTokenData.slice(0, headerBytes.length)
    if (!Arrays.equals(header, headerBytes)) {
      throw new InvalidDataException("Authentication token header not found");
    }
    val version = decryptedTokenData.slice(headerBytes.length, headerBytes.length + versionBytes.length)
    if (!Arrays.equals(version, versionBytes)) {
      throw new InvalidDataException("Unsupported version " + new String(version, encodingCharset));
    }
    val payloadStart = headerBytes.length + versionBytes.length
    val tokenData = decryptedTokenData.slice(payloadStart, decryptedTokenData.length)
    decodeToken(tokenData)
  }
}