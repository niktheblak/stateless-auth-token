package serialization

import BinaryUtils._
import scala.collection.mutable.ListBuffer
import java.io.ByteArrayOutputStream

object FieldEncoder {
  def encode(items: Seq[Any]): Array[Byte] = {
    val target = new ByteArrayOutputStream
    items foreach { item ⇒
      DefaultSerializers.serializerFor(item) match {
        case Some(serializer) ⇒
          val data = serializer.serialize(item)
          target.write(data)
        case None ⇒ throw new IllegalArgumentException(s"No serializer found for class ${item.getClass}")
      }
    }
    target.toByteArray
  }

  def decode(data: Array[Byte]): Seq[Any] = {
    val buf = new ListBuffer[Any]()
    var position = 0
    while (position < data.size) {
      val (item, size) = decodeItem[Any](data, position)
      position += size + 1
      buf += item
    }
    buf.toSeq
  }

  private def decodeItem[T](source: Array[Byte], offset: Int): (T, Int) = {
    val (id, size) = unpack(source(offset))
    DefaultSerializers.serializerForId(id) match {
      case Some(serializer) ⇒
        val value = serializer.deSerialize(source, offset)
        (value.asInstanceOf[T], size)
      case None ⇒ throw new IllegalArgumentException(s"No serializer found for serial ID $id")
    }
  }
}