package tokens

trait TokenEncoder {
  def encodeToken(auth: Authentication): Array[Byte]
  def decodeToken(tokenData: Array[Byte]): Authentication
}
