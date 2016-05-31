package token

import java.nio.charset.Charset

import auth.{Authentication, AuthenticationException}
import encoding.{Base58StringEncoder, PayloadEncoder, TokenEncoder}
import org.jasypt.util.binary.{BasicBinaryEncryptor, BinaryEncryptor}

trait JasyptTokenCreator extends PayloadEncoder with Base58StringEncoder { self: TokenEncoder ⇒
  val header = "AUTH"
  val version = 3
  val encodingCharset = Charset.forName("UTF-8")
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
    try {
      val decrypted = encryptor.decrypt(tokenData)
      val payload = decodePayload(decrypted)
      decodeToken(payload)
    } catch {
      case e: Exception ⇒ throw new AuthenticationException("Error while decoding token data", e)
    }
  }

  def createTokenString(auth: Authentication): String =
    encodeString(createToken(auth))

  def readTokenString(tokenString: String): Authentication = {
    val decoded = decodeString(tokenString)
    readToken(decoded)
  }
}
