package tokens

import org.jasypt.util.binary.{BinaryEncryptor, BasicBinaryEncryptor}

trait JasyptTokenCreator extends PayloadEncoder with Base58StringEncoder { self: TokenEncoder ⇒
  val header = "AUTH"
  val version = 3
  val encodingCharset = "UTF-8"
  val headerBytes = header.getBytes(encodingCharset)
  val versionBytes = Array[Byte](version.toByte)

  def passPhrase: String

  val encryptor: BinaryEncryptor = {
    val encryptor = new BasicBinaryEncryptor
    encryptor.setPassword(passPhrase)
    encryptor
  }

  def createToken(auth: Authentication): Array[Byte] = {
    val encodedToken: Array[Byte] = encodeToken(auth)
    val payload = encodePayload(encodedToken)
    encryptor.encrypt(payload)
  }

  def readToken(tokenData: Array[Byte]): Authentication = {
    val decrypted = encryptor.decrypt(tokenData)
    val payload = decodePayload(decrypted)
    decodeToken(payload)
  }

  def createTokenString(auth: Authentication): String =
    encodeString(createToken(auth))

  def readTokenString(tokenString: String): Authentication = {
    val decoded = decodeString(tokenString)
    readToken(decoded)
  }
}
