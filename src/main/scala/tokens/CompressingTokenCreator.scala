package tokens

import java.util.zip.{Inflater, Deflater}
import java.nio.ByteBuffer
import utils.ByteBuffers
import base58.Base58
import utils.ByteBuffers._
import tokens.Authentication
import serialization.InvalidDataException

trait CompressingTokenCreator extends AESSharedKeyEncrypter { self: TokenEncoder ⇒
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
    val deflated = compress(encryptedToken)
    Base58.encode(deflated)
  }

  def decodeAuthToken(tokenString: String): Authentication = {
    val encryptedTokenData = Base58.decode(tokenString)
    val encrypted = ByteBuffer.wrap(encryptedTokenData)
    val decrypted = ByteBuffer.allocate(256)
    decrypt(encrypted, passPhrase.toCharArray, salt, decrypted)
    decrypted.limit(decrypted.position())
    decrypted.rewind()
    val decompressed = decompress(decrypted)
    val header = read(decompressed, headerBytes.length)
    checkData(headerBytes, header, "Authentication token header not found")
    val version = read(decompressed, versionBytes.length)
    checkData(versionBytes, version, "Unsupported version " + new String(version, encodingCharset))
    decodeToken(decompressed)
  }

  def checkData(expected: Array[Byte], actual: Array[Byte], message: String) {
    if (!java.util.Arrays.equals(expected, actual)) {
      throw new InvalidDataException(message)
    }
  }

  def compress(data: ByteBuffer): Array[Byte] = {
    val bytes = ByteBuffers.toByteArray(data)
    val deflater = new Deflater()
    deflater.setInput(bytes)
    deflater.finish()
    val deflatedData = new Array[Byte](256)
    val size = deflater.deflate(deflatedData)
    deflatedData.slice(0, size)
  }

  def decompress(data: ByteBuffer): ByteBuffer = {
    val inflater = new Inflater()
    val compressed = ByteBuffers.toByteArray(data)
    inflater.setInput(compressed)
    val decompressed = new Array[Byte](256)
    val size = inflater.inflate(decompressed)
    ByteBuffer.wrap(decompressed, 0, size)
  }
}
