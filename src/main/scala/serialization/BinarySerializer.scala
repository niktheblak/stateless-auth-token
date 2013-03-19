package serialization

import java.nio.ByteBuffer

trait BinarySerializer[T] {
  def serialize(obj: T, target: ByteBuffer)
  def deSerialize(source: ByteBuffer): T
}

trait BitOps {
  def pack(id: Int, size: Int): Int = {
    require(id <= 3, "id must be smaller than 4")
    require(size <= 63, "size must be smaller than 64")
    val packed = ((id & 0x3) << 6) | (size & 0x3F)
    packed
  }

  def unpack(x: Int): (Int, Int) = {
    val id = x >> 6
    val size = x & 0x3F
    (id, size)
  }
}

object DefaultSerializers extends BitOps {
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
      val buf = new Array[Byte](size)
      source.get(buf)
      new String(buf, "UTF-8")
    }
  }

  object StringSerializer {
    val identifier = 0
  }

  class LongSerializer extends BinarySerializer[Long] with BitOps {
    import LongSerializer.identifier
    def serialize(value: Long, target: ByteBuffer) {
      if (value < Byte.MaxValue) {
        val idAndSize = pack(identifier, 1).toByte
        target.put(idAndSize)
        target.put(value.toByte)
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
        case 1 => source.get().toLong
        case 4 =>
          val intBuf = source.asIntBuffer()
          source.position(source.position() + 4)
          intBuf.get()
        case 8 =>
          val longBuf = source.asLongBuffer()
          source.position(source.position() + 8)
          longBuf.get()
        case n => throw new InvalidDataException("Unsupported data size " + n)
      }
    }
  }

  object LongSerializer {
    val identifier = 1
  }
}