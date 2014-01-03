package tokens

import base58.Base58

trait ConstantSaltTokenCreator extends PayloadEncoder { self: TokenEncoder with Encrypter ⇒
  val header = "AUTH-TOKEN"
  val version = "1.0"
  val encodingCharset = "UTF-8"
  val headerBytes = header.getBytes(encodingCharset)
  val versionBytes = version.getBytes(encodingCharset)

  def passPhrase: String
  def salt: Array[Byte]
    
  def createAuthToken(auth: Authentication): String = {
    val payload = encodePayload(encodeToken(auth), passPhrase.toCharArray, salt)
    Base58.encode(payload)
  }
  
  def decodeAuthToken(tokenString: String): Authentication = {
    val encrypted = Base58.decode(tokenString)
    val payload = decodePayload(encrypted, passPhrase.toCharArray, salt)
    decodeToken(payload)
  }
}