package tokens

import serialization.VariableLengthIntegerCodec._
import java.nio.ByteBuffer
import java.util.Date

trait VariableLengthIntegerTokenEncoder extends TokenEncoder {
  def encodeToken(auth: Authentication, buffer: ByteBuffer) {
    val userIdBytes = auth.userId.getBytes("UTF-8")
    val roleBytes = auth.role.getBytes("UTF-8")
    buffer.put(encode(userIdBytes.size))
    buffer.put(userIdBytes)
    buffer.put(encode(roleBytes.size))
    buffer.put(roleBytes)
    buffer.put(encode(auth.expirationTime.getTime))
  }

  def decodeToken(tokenData: ByteBuffer): Authentication = {
    val userIdLength = decode(tokenData).toInt
    val userIdBytes = new Array[Byte](userIdLength)
    tokenData.get(userIdBytes)
    val userId = new String(userIdBytes, "UTF-8")
    val roleLength = decode(tokenData).toInt
    val roleBytes = new Array[Byte](roleLength)
    tokenData.get(roleBytes)
    val role = new String(roleBytes, "UTF-8")
    val expirationTime = decode(tokenData)
    Authentication(userId, role, new Date(expirationTime))
  }
}
