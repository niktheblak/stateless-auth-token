package serialization

import scala.collection.mutable.ListBuffer

object FieldEncoder extends BitOps {
  def encode[T](items: Seq[T]): Array[Byte] = {
    items.foldLeft(Array[Byte]()) { (acc, x) =>
      DefaultSerializers.serializerFor(x) match {
        case Some(serializer) =>
          val data = serializer.serialize(x)
          acc ++ data
        case None => throw new IllegalArgumentException("No serializer found for class " + x.getClass)
      }
    }
  }

  def decode(data: Array[Byte]): Seq[Any] = {
    val buf = new ListBuffer[Any]()
    var i = 0
    while (i < data.length) {
      val (_, len) = unpack(data(i))
      val end = i + len
      assert(end <= data.length)
      buf += decodeItem(data.slice(i, end + 1))
      i += len + 1
    }
    buf.toSeq
  }

  private def decodeItem[T](data: Array[Byte]): T = {
    val (id, _) = unpack(data(0))
    DefaultSerializers.serializerForId(id) match {
      case Some(serializer) =>
        val value = serializer.deSerialize(data)
        value.asInstanceOf[T]
      case None => throw new IllegalArgumentException("No serializer found for serial ID " + id)
    }
  }
}