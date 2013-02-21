package tokens

import scala.collection.mutable.ListBuffer

trait BinarySerializer[T] {
  def serialize(obj: T): Array[Byte]
  def deserialize(data: Array[Byte]): T
}

object FieldEncoder {
  implicit object StringSerializer extends BinarySerializer[String] {
    def serialize(obj: String): Array[Byte] =
      obj.getBytes("UTF-8")

    def deserialize(data: Array[Byte]): String =
      new String(data, "UTF-8")
  }

  def encode[T](items: Seq[T])(implicit serializer: BinarySerializer[T]): Array[Byte] = {
    items.foldLeft(Array[Byte]()) { (acc, x) =>
      val data = serializer.serialize(x)
      require(data.length <= Byte.MaxValue)
      val len = data.length.toByte
      acc ++ Array(len) ++ data
    }
  }

  def decode[T](data: Array[Byte])(implicit serializer: BinarySerializer[T]): Seq[T] = {
    val buf = new ListBuffer[T]()
    var i = 0
    while (i < data.length) {
      val len = data(i).toInt
      require(len < data.length - i)
      buf += decodeItem(data, i + 1, len)(serializer)
      i += len + 1
    }
    buf.toList
  }

  private def decodeItem[T](data: Array[Byte], offset: Int, length: Int)(serializer: BinarySerializer[T]): T = {
    require(offset >= 0)
    val end = offset + length
    require(end <= data.length)
    val itemData = data.slice(offset, end)
    serializer.deserialize(itemData)
  }
}