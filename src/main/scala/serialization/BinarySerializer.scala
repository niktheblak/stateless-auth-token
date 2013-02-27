package serialization

import java.nio.ByteBuffer

trait BinarySerializer[T] {
  def serialize(obj: T): Array[Byte]
  def deSerialize(data: Array[Byte]): T
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

    def serialize(obj: String): Array[Byte] = {
      val bytes = obj.getBytes("UTF-8")
      val idAndSize = pack(identifier, bytes.length)
      Array(idAndSize.toByte) ++ bytes
    }

    def deSerialize(data: Array[Byte]): String = {
      val (id, size) = unpack(data(0))
      require(id == identifier, "Serial ID %d does not match expected %d".format(id, identifier))
      new String(data, 1, size, "UTF-8")
    }
  }

  object StringSerializer {
    val identifier = 0
  }

  class LongSerializer extends BinarySerializer[Long] with BitOps {
    import LongSerializer.identifier
    def serialize(value: Long): Array[Byte] = {
      if (value < Byte.MaxValue) {
        val buf = ByteBuffer.allocate(2)
        val idAndSize = pack(identifier, 1).toByte
        buf.put(idAndSize)
        buf.put(value.toByte)
        buf.array()
      } else if (value < Int.MaxValue) {
        val buf = ByteBuffer.allocate(5)
        val idAndSize = pack(identifier, 4).toByte
        buf.put(idAndSize)
        buf.putInt(value.toInt)
        buf.array()
      } else {
        val buf = ByteBuffer.allocate(9)
        val idAndSize = pack(identifier, 8).toByte
        buf.put(idAndSize)
        buf.putLong(value)
        buf.array()
      }
    }

    def deSerialize(data: Array[Byte]): Long = {
      val buf = ByteBuffer.allocate(data.length)
      buf.put(data)
      buf.rewind()
      val idAndSize = buf.get()
      val (id, size) = unpack(idAndSize)
      require(id == identifier, "Serial ID %d does not match expected %d".format(id, identifier))
      size match {
        case 1 => buf.get().toLong
        case 4 =>
          val intBuf = buf.asIntBuffer()
          intBuf.get()
        case 8 =>
          val longBuf = buf.asLongBuffer()
          longBuf.get()
        case n => throw new InvalidDataException("Unsupported data size " + n)
      }
    }
  }

  object LongSerializer {
    val identifier = 1
  }
}