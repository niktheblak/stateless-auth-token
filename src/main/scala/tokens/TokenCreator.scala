package tokens

import base58.Base58
import java.util.Arrays
import serialization.InvalidDataException
import java.nio.ByteBuffer

trait TokenCreator extends FieldEncoderTokenEncoder with AESSharedKeyEncrypter {
  val header = "AUTH-TOKEN"
  val version = "1.0"
  val encodingCharset = "UTF-8"
  val headerBytes = header.getBytes(encodingCharset)
  val versionBytes = version.getBytes(encodingCharset)

  def passPhrase: String
  def salt: Array[Byte]
    
  def createAuthToken(auth: Authentication): String = {
    val tokenData = ByteBuffer.allocate(256)
    tokenData.put(headerBytes)
    tokenData.put(versionBytes)
    encodeToken(auth, tokenData)
    tokenData.limit(tokenData.position)
    tokenData.rewind()
    val encryptedToken = ByteBuffer.allocate(256)
    encrypt(tokenData, passPhrase.toCharArray, salt, encryptedToken)
    Base58.encode(toBytes(encryptedToken))
  }

  def toBytes(buf: ByteBuffer): Array[Byte] = {
    val bytes = new Array[Byte](buf.position)
    buf.rewind()
    buf.get(bytes)
    bytes
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