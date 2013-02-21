package serialization

import scala.collection.mutable.ListBuffer

object FieldEncoder {
  implicit object StringSerializer extends BinarySerializer[String] {
    val identifier = 0.toByte
    def serialize(obj: String): Array[Byte] =
      obj.getBytes("UTF-8")

    def deSerialize(data: Array[Byte]): String =
      new String(data, "UTF-8")
  }

  def encode[T](items: Seq[T]): Array[Byte] = {
    items.foldLeft(Array[Byte]()) { (acc, x) =>
      val serializer = DefaultSerializers.serializerFor(x)
      val data = serializer.serialize(x)
      require(data.length <= Byte.MaxValue)
      val len = data.length.toByte
      acc ++ Array(len) ++ data
    }
  }

  def decode(data: Array[Byte]): Seq[Any] = {
    val buf = new ListBuffer[Any]()
    var i = 0
    while (i < data.length) {
      val len = data(i).toInt
      require(len < data.length - i)
      buf += decodeItem(data, i + 1, len)
      i += len + 1
    }
    buf.toList
  }

  private def decodeItem[T](data: Array[Byte], offset: Int, length: Int): T = {
    require(offset >= 0)
    val end = offset + length
    require(end <= data.length)
    val itemData = data.slice(offset, end)
    val serializer = DefaultSerializers.serializerForId(itemData(0))
    val value = serializer.deSerialize(itemData)
    value.asInstanceOf[T]
  }
}