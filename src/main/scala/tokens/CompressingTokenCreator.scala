package tokens

import java.util.zip.{Inflater, Deflater}
import base58.Base58
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
    val tokenData = headerBytes ++ versionBytes ++ encodeToken(auth)
    val encrypted = encrypt(tokenData, passPhrase.toCharArray, salt)
    val deflated = compress(encrypted)
    Base58.encode(deflated)
  }

  def decodeAuthToken(tokenString: String): Authentication = {
    val encrypted = Base58.decode(tokenString)
    val decrypted = decrypt(encrypted, passPhrase.toCharArray, salt)
    val decompressed = decompress(decrypted)
    val header = decompressed.slice(0, headerBytes.length)
    checkData(headerBytes, header, "Authentication token header not found")
    val versionStart = headerBytes.size
    val versionEnd = versionStart + versionBytes.size
    val version = decompressed.slice(versionStart, versionEnd)
    checkData(versionBytes, version, "Unsupported version " + new String(version, encodingCharset))
    decodeToken(decompressed.slice(versionEnd, decompressed.size))
  }

  def checkData(expected: Array[Byte], actual: Array[Byte], message: String) {
    if (!java.util.Arrays.equals(expected, actual)) {
      throw new InvalidDataException(message)
    }
  }

  def compress(data: Array[Byte]): Array[Byte] = {
    val deflater = new Deflater()
    deflater.setInput(data)
    deflater.finish()
    val deflatedData = new Array[Byte](256)
    val size = deflater.deflate(deflatedData)
    deflatedData.slice(0, size)
  }

  def decompress(data: Array[Byte]): Array[Byte] = {
    val inflater = new Inflater()
    inflater.setInput(data)
    val decompressed = new Array[Byte](256)
    val size = inflater.inflate(decompressed)
    decompressed.slice(0, size)
  }
}
