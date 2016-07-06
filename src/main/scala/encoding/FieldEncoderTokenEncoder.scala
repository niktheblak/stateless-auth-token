package encoding

import java.util.Date

import auth.{ Authentication, Roles }
import serialization.{ FieldEncoder, InvalidDataException }

trait FieldEncoderTokenEncoder extends TokenEncoder {
  override def encodeToken(auth: Authentication): Array[Byte] = {
    val items: Seq[Any] = Seq(auth.userId, auth.role, auth.expirationTime.getTime)
    FieldEncoder.encode(items)
  }

  override def decodeToken(tokenData: Array[Byte]): Authentication = {
    val fields: Seq[Any] = FieldEncoder.decode(tokenData)
    if (fields.length != 3) {
      throw new InvalidDataException("Malformed content")
    }
    val userId = fields(0).asInstanceOf[String]
    val role = fields(1).asInstanceOf[Roles.Role]
    val expirationTime = fields(2).asInstanceOf[Long]
    Authentication(userId, role, new Date(expirationTime))
  }
}