package tokens

trait ConstantSaltTokenCreator extends PayloadEncoder with Base58StringEncoder { self: TokenEncoder with Encrypter ⇒
  val header = "AUTH-TOKEN"
  val version = "1.0"
  val encodingCharset = "UTF-8"
  val headerBytes = header.getBytes(encodingCharset)
  val versionBytes = version.getBytes(encodingCharset)

  def passPhrase: String
  def salt: Array[Byte]
    
  def createAuthToken(auth: Authentication): String = {
    val payload = encodePayload(encodeToken(auth))
    val encrypted = encrypt(payload, passPhrase.toCharArray, salt)
    encodeString(encrypted)
  }
  
  def decodeAuthToken(tokenString: String): Authentication = {
    val encrypted = decodeString(tokenString)
    val decrypted = decrypt(encrypted, passPhrase.toCharArray, salt)
    val payload = decodePayload(decrypted)
    decodeToken(payload)
  }
}