package encoding

import auth.Authentication

trait TokenEncoder {
  def encodeToken(auth: Authentication): Array[Byte]
  def decodeToken(tokenData: Array[Byte]): Authentication
}
