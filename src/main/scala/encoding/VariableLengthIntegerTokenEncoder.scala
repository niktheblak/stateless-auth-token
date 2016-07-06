package encoding

import java.nio.ByteBuffer
import java.util.Date

import auth.Authentication
import serialization.DefaultSerializers
import serialization.VariableLengthIntegerCodec._

trait VariableLengthIntegerTokenEncoder extends TokenEncoder {
  private val roleSerializer = new DefaultSerializers.RoleSerializer

  override def encodeToken(auth: Authentication): Array[Byte] = {
    val userIdBytes = auth.userId.getBytes("UTF-8")
    val roleBytes = roleSerializer.serialize(auth.role)
    encode(userIdBytes.length) ++
      userIdBytes ++
      encode(roleBytes.length) ++
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
    val role = roleSerializer.deSerialize(roleBytes, 0)
    val expirationTime = decode(buffer)
    Authentication(userId, role, new Date(expirationTime))
  }
}
