package tokens

import base58.Base58
import serialization.InvalidDataException
import utils.ByteBuffers.{toByteArray, read}
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
    Base58.encode(toByteArray(encryptedToken))
  }
  
  def decodeAuthToken(tokenString: String): Authentication = {
    val encryptedTokenData = Base58.decode(tokenString)
    val encrypted = ByteBuffer.wrap(encryptedTokenData)
    val decrypted = ByteBuffer.allocate(256)
    decrypt(encrypted, passPhrase.toCharArray, salt, decrypted)
    decrypted.limit(decrypted.position())
    decrypted.rewind()
    val header = read(decrypted, headerBytes.length)
    checkData(headerBytes, header, "Authentication token header not found")
    val version = read(decrypted, versionBytes.length)
    checkData(versionBytes, version, "Unsupported version " + new String(version, encodingCharset))
    decodeToken(decrypted)
  }

  def checkData(expected: Array[Byte], actual: Array[Byte], message: String) {
    if (!java.util.Arrays.equals(expected, actual)) {
      throw new InvalidDataException(message)
    }
  }
}