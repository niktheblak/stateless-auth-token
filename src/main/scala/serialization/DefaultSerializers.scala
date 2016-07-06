package serialization

import java.nio.ByteBuffer
import java.nio.charset.Charset

import auth.Roles
import serialization.BinaryUtils._
import utils.ByteBuffers

object DefaultSerializers {
  private val encodingCharset = Charset.forName("UTF-8")

  private val serializers: Map[Int, BinarySerializer[_]] = Map(
    StringSerializer.identifier → new StringSerializer,
    LongSerializer.identifier → new LongSerializer,
    RoleSerializer.identifier → new RoleSerializer
  )

  def serializerFor[T](obj: T): Option[BinarySerializer[T]] = {
    require(obj != null)
    obj match {
      case _: String ⇒ Some(serializers(StringSerializer.identifier).asInstanceOf[BinarySerializer[T]])
      case _: java.lang.Byte ⇒ Some(serializers(LongSerializer.identifier).asInstanceOf[BinarySerializer[T]])
      case _: java.lang.Short ⇒ Some(serializers(LongSerializer.identifier).asInstanceOf[BinarySerializer[T]])
      case _: java.lang.Integer ⇒ Some(serializers(LongSerializer.identifier).asInstanceOf[BinarySerializer[T]])
      case _: java.lang.Long ⇒ Some(serializers(LongSerializer.identifier).asInstanceOf[BinarySerializer[T]])
      case _: Roles.Role ⇒ Some(serializers(RoleSerializer.identifier).asInstanceOf[BinarySerializer[T]])
      case _ ⇒ None
    }
  }

  def serializerForId(id: Int): Option[BinarySerializer[_]] = {
    serializers.get(id)
  }

  class StringSerializer extends BinarySerializer[String] {
    import StringSerializer.identifier

    def serialize(obj: String): Array[Byte] = {
      val bytes = obj.getBytes(encodingCharset)
      require(
        bytes.length <= BinaryUtils.maxSizeLength,
        s"Source string is ${bytes.length} bytes with $encodingCharset encoding, maximum is ${BinaryUtils.maxSizeLength} bytes"
      )
      val idAndSize = pack(identifier, bytes.length)
      Array(idAndSize.toByte) ++ bytes
    }

    def deSerialize(source: Array[Byte], offset: Int): String = {
      val (id, size) = unpack(source(offset))
      require(id == identifier, s"Serial ID $id does not match expected $identifier")
      new String(source, offset + 1, size, encodingCharset)
    }
  }

  object StringSerializer {
    val identifier = 0
  }

  class LongSerializer extends BinarySerializer[Long] {
    import LongSerializer._

    def serialize(value: Long): Array[Byte] = {
      val target = ByteBuffer.allocate(9)
      if (value < Byte.MaxValue) {
        val idAndSize = pack(identifier, 1).toByte
        target.put(idAndSize)
        target.put(value.toByte)
      } else if (value < Short.MaxValue) {
        val idAndSize = pack(identifier, 2).toByte
        target.put(idAndSize)
        target.putShort(value.toShort)
      } else if (value < Int.MaxValue) {
        val idAndSize = pack(identifier, 4).toByte
        target.put(idAndSize)
        target.putInt(value.toInt)
      } else {
        val idAndSize = pack(identifier, 8).toByte
        target.put(idAndSize)
        target.putLong(value)
      }
      ByteBuffers.toByteArray(target)
    }

    def deSerialize(data: Array[Byte], offset: Int): Long = {
      val idAndSize = data(offset)
      val (id, size) = unpack(idAndSize)
      val source = ByteBuffer.wrap(data, offset + 1, size)
      require(id == identifier, s"Serial ID $id does not match expected $identifier")
      size match {
        case 1 ⇒
          source.get().toLong
        case 2 ⇒
          val shortBuf = source.asShortBuffer()
          shortBuf.get()
        case 4 ⇒
          val intBuf = source.asIntBuffer()
          intBuf.get()
        case 8 ⇒
          val longBuf = source.asLongBuffer()
          longBuf.get()
        case n ⇒ throw new InvalidDataException(s"Unsupported data size $n")
      }
    }
  }

  object LongSerializer {
    val identifier = 1
  }

  class RoleSerializer extends BinarySerializer[Roles.Role] {
    import RoleSerializer._

    override def serialize(role: Roles.Role): Array[Byte] = {
      val bytes = new Array[Byte](2)
      val idAndSize = pack(identifier, 1).toByte
      bytes.update(0, idAndSize)
      role match {
        case Roles.Admin ⇒ bytes.update(1, 0.toByte)
        case Roles.User ⇒ bytes.update(1, 1.toByte)
      }
      bytes
    }

    override def deSerialize(data: Array[Byte], offset: Int): Roles.Role = {
      val idAndSize = data(offset)
      val (id, size) = unpack(idAndSize)
      require(id == identifier, s"Serial ID $id does not match expected $identifier")
      require(size == 1, s"Invalid size $size")
      data(offset + 1) match {
        case 0 ⇒ Roles.Admin
        case 1 ⇒ Roles.User
      }
    }
  }

  object RoleSerializer {
    val identifier = 2
  }
}
