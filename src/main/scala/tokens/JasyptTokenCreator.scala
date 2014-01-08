package tokens

import org.jasypt.util.binary.{BinaryEncryptor, BasicBinaryEncryptor}

trait JasyptTokenCreator extends PayloadEncoder with Base58StringEncoder { self: TokenEncoder ⇒
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

  def createTokenInternal(auth: Authentication): Array[Byte] = {
    val encodedToken: Array[Byte] = encodeToken(auth)
    val payload = encodePayload(encodedToken)
    encryptor.encrypt(payload)
  }

  def decodeTokenInternal(tokenData: Array[Byte]): Authentication = {
    val decrypted = encryptor.decrypt(tokenData)
    val payload = decodePayload(decrypted)
    decodeToken(payload)
  }

  def createAuthToken(auth: Authentication): String =
    encode(createTokenInternal(auth))

  def decodeAuthToken(tokenString: String): Authentication = {
    val decoded = decode(tokenString)
    decodeTokenInternal(decoded)
  }
}
