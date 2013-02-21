package serialization

import scala.collection.mutable.ListBuffer

object FieldEncoder {
  def encode[T](items: Seq[T]): Array[Byte] = {
    items.foldLeft(Array[Byte]()) { (acc, x) =>
      DefaultSerializers.serializerFor(x) match {
        case Some(serializer) =>
          val data = serializer.serialize(x)
          require(data.length <= Byte.MaxValue)
          val len = data.length.toByte
          acc ++ Array(len) ++ data
        case None => throw new IllegalArgumentException("No serializer found for class " + x.getClass)
      }
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
    val id = itemData(0)
    DefaultSerializers.serializerForId(id) match {
      case Some(serializer) =>
        val value = serializer.deSerialize(itemData)
        value.asInstanceOf[T]
      case None => throw new IllegalArgumentException("No serializer found for serial ID " + id)
    }
  }
}