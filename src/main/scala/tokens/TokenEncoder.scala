package tokens

import java.nio.ByteBuffer

trait TokenEncoder {
  def encodeToken(auth: Authentication, buffer: ByteBuffer)
  def decodeToken(tokenData: ByteBuffer): Authentication
}
