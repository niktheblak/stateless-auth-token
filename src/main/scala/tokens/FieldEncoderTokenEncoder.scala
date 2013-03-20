package tokens

import java.nio.ByteBuffer
import serialization.{InvalidDataException, FieldEncoder}
import java.util.Date

trait FieldEncoderTokenEncoder {
  def encodeToken(auth: Authentication, buffer: ByteBuffer) {
    require(auth.userId.length <= Byte.MaxValue, "Too long userId")
    require(auth.role.length <= Byte.MaxValue, "Too long role")
    val items = Seq(auth.userId, auth.role, auth.expirationTime.getTime)
    FieldEncoder.encode(items, buffer)
  }

  def decodeToken(tokenData: Array[Byte]): Authentication = {
    val fields: Seq[Any] = FieldEncoder.decode(ByteBuffer.wrap(tokenData))
    if (fields.length != 3) {
      throw new InvalidDataException("Malformed content");
    }
    val userId = fields(0).asInstanceOf[String]
    val role = fields(1).asInstanceOf[String]
    val expirationTime = fields(2).asInstanceOf[Long]
    Authentication(userId, role, new Date(expirationTime))
  }
}
