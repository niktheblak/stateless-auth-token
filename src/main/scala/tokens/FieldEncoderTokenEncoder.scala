package tokens

import serialization.{InvalidDataException, FieldEncoder}
import java.util.Date

trait FieldEncoderTokenEncoder extends TokenEncoder {
  def encodeToken(auth: Authentication): Array[Byte] = {
    require(auth.userId.length <= Byte.MaxValue, "Too long userId")
    require(auth.role.length <= Byte.MaxValue, "Too long role")
    val items = Seq(auth.userId, auth.role, auth.expirationTime.getTime)
    FieldEncoder.encode(items)
  }

  def decodeToken(tokenData: Array[Byte]): Authentication = {
    val fields: Seq[Any] = FieldEncoder.decode(tokenData)
    if (fields.length != 3) {
      throw new InvalidDataException("Malformed content")
    }
    val userId = fields(0).asInstanceOf[String]
    val role = fields(1).asInstanceOf[String]
    val expirationTime = fields(2).asInstanceOf[Long]
    Authentication(userId, role, new Date(expirationTime))
  }
}
