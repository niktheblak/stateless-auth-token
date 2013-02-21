package serialization

import java.nio.ByteBuffer

trait BinarySerializer[T] {
  def serialize(obj: T): Array[Byte]
  def deSerialize(data: Array[Byte]): T
}

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
    import StringSerializer.identifier
    def serialize(obj: String): Array[Byte] =
      Array(identifier.toByte) ++ obj.getBytes("UTF-8")

    def deSerialize(data: Array[Byte]): String = {
      val id = data(0)
      require(id == identifier, "Serial ID %d does not match expected %d".format(id, identifier))
      new String(data.slice(1, data.length), "UTF-8")
    }
  }

  object StringSerializer {
    val identifier = 0
  }

  class LongSerializer extends BinarySerializer[Long] {
    import LongSerializer.identifier
    def serialize(value: Long): Array[Byte] = {
      if (value < Byte.MaxValue) {
        val buf = ByteBuffer.allocate(3)
        buf.put(identifier.toByte)
        buf.put(1.toByte)
        buf.put(value.toByte)
        buf.array()
      } else if (value < Int.MaxValue) {
        val buf = ByteBuffer.allocate(6)
        buf.put(identifier.toByte)
        buf.put(4.toByte)
        buf.putInt(value.toInt)
        buf.array()
      } else {
        val buf = ByteBuffer.allocate(10)
        buf.put(identifier.toByte)
        buf.put(8.toByte)
        buf.putLong(value)
        buf.array()
      }
    }

    def deSerialize(data: Array[Byte]): Long = {
      val buf = ByteBuffer.allocate(data.length)
      buf.put(data)
      buf.rewind()
      val id = buf.get()
      require(id == identifier, "Serial ID %d does not match expected %d".format(id, identifier))
      val size = buf.get()
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