package serialization

import java.nio.ByteBuffer
import utils.ByteBuffers

object DefaultSerializers {
  private val serializers: Map[Int, BinarySerializer[_]] = Map(
    StringSerializer.identifier -> new StringSerializer,
    LongSerializer.identifier -> new LongSerializer)

  def serializerFor[T](obj: T): Option[BinarySerializer[T]] = {
    require(obj != null)
    if (obj.getClass == classOf[String])
      Some((new StringSerializer).asInstanceOf[BinarySerializer[T]])
    else if (obj.getClass == classOf[java.lang.Long])
      Some((new LongSerializer).asInstanceOf[BinarySerializer[T]])
    else None
  }

  def serializerForId(id: Int): Option[BinarySerializer[_]] = {
    serializers.get(id)
  }

  class StringSerializer extends BinarySerializer[String] {
    import BinaryUtils._
    import StringSerializer.identifier

    def serialize(obj: String, target: ByteBuffer) {
      val bytes = obj.getBytes("UTF-8")
      val idAndSize = pack(identifier, bytes.length)
      target.put(idAndSize.toByte)
      target.put(bytes)
    }

    def deSerialize(source: ByteBuffer): String = {
      val (id, size) = unpack(source.get())
      require(id == identifier, "Serial ID %d does not match expected %d".format(id, identifier))
      val buf = ByteBuffers.read(source, size)
      new String(buf, "UTF-8")
    }
  }

  object StringSerializer {
    val identifier = 0
  }

  class LongSerializer extends BinarySerializer[Long] {
    import BinaryUtils._
    import LongSerializer.identifier

    def serialize(value: Long, target: ByteBuffer) {
      if (value < Byte.MaxValue) {
        val idAndSize = pack(identifier, 1).toByte
        target.put(idAndSize)
        target.put(value.toByte)
      } else if (value < Short.MaxValue ) {
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
    }

    def deSerialize(source: ByteBuffer): Long = {
      val idAndSize = source.get()
      val (id, size) = unpack(idAndSize)
      require(id == identifier, "Serial ID %d does not match expected %d".format(id, identifier))
      size match {
        case 1 ⇒ source.get().toLong
        case 2 ⇒
          val shortBuf = source.asShortBuffer()
          source.position(source.position() + 2)
          shortBuf.get()
        case 4 ⇒
          val intBuf = source.asIntBuffer()
          source.position(source.position() + 4)
          intBuf.get()
        case 8 ⇒
          val longBuf = source.asLongBuffer()
          source.position(source.position() + 8)
          longBuf.get()
        case n ⇒ throw new InvalidDataException("Unsupported data size " + n)
      }
    }
  }

  object LongSerializer {
    val identifier = 1
  }
}
