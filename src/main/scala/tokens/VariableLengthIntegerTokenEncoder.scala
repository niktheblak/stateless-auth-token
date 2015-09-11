package tokens

import serialization.VariableLengthIntegerCodec._
import java.nio.ByteBuffer
import java.util.Date

trait VariableLengthIntegerTokenEncoder extends TokenEncoder {
  override def encodeToken(auth: Authentication): Array[Byte] = {
    val userIdBytes = auth.userId.getBytes("UTF-8")
    val roleBytes = auth.role.getBytes("UTF-8")
    encode(userIdBytes.size) ++
      userIdBytes ++
      encode(roleBytes.size) ++
      roleBytes ++
      encode(auth.expirationTime.getTime)
  }

  override def decodeToken(tokenData: Array[Byte]): Authentication = {
    val buffer = ByteBuffer.wrap(tokenData)
    val userIdLength = decode(buffer).toInt
    val userIdBytes = new Array[Byte](userIdLength)
    buffer.get(userIdBytes)
    val userId = new String(userIdBytes, "UTF-8")
    val roleLength = decode(buffer).toInt
    val roleBytes = new Array[Byte](roleLength)
    buffer.get(roleBytes)
    val role = new String(roleBytes, "UTF-8")
    val expirationTime = decode(buffer)
    Authentication(userId, role, new Date(expirationTime))
  }
}
