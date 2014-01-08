package tokens

import org.jasypt.util.binary.{BinaryEncryptor, BasicBinaryEncryptor}
import base58.Base58

trait JasyptTokenCreator extends PayloadEncoder { self: TokenEncoder ⇒
  val header = "AUTH"
  val version = 3
  val encodingCharset = "UTF-8"
  val headerBytes = header.getBytes(encodingCharset)
  val versionBytes = Array[Byte](version.toByte)

  def passPhrase: String

  lazy val encryptor: BinaryEncryptor = {
    val encryptor = new BasicBinaryEncryptor
    encryptor.setPassword(passPhrase)
    encryptor
  }

  def createAuthToken(auth: Authentication): String = {
    val payload = encodePayload(encodeToken(auth))
    val encrypted = encryptor.encrypt(payload)
    Base58.encode(encrypted)
  }

  def decodeAuthToken(tokenString: String): Authentication = {
    val decoded = Base58.decode(tokenString)
    val decrypted = encryptor.decrypt(decoded)
    val tokenData = decodePayload(decrypted)
    decodeToken(tokenData)
  }
}
