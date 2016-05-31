package token

import auth.{Authentication, AuthenticationException}
import crypto.Encryptor
import encoding.{Base58StringEncoder, PayloadEncoder, TokenEncoder}

trait EncryptingTokenCreator extends PayloadEncoder with Base58StringEncoder { self: TokenEncoder with Encryptor ⇒
  def generateSalt: Array[Byte] = Array.emptyByteArray

  def createToken(auth: Authentication): Array[Byte] = {
    val encodedToken: Array[Byte] = encodeToken(auth)
    val payload = encodePayload(encodedToken)
    encrypt(payload, generateSalt)
  }

  def readToken(tokenData: Array[Byte]): Authentication = {
    try {
      val decrypted = decrypt(tokenData, generateSalt)
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
