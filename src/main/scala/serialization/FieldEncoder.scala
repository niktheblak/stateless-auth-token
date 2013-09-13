package serialization

import scala.collection.mutable.ListBuffer
import java.nio.ByteBuffer
import utils.ByteBuffers

object FieldEncoder {
  import BinaryUtils._

  def encode[T](items: Seq[T]): Array[Byte] = {
    val target = ByteBuffer.allocate(1024)
    items foreach { item =>
      DefaultSerializers.serializerFor(item) match {
        case Some(serializer) => serializer.serialize(item, target)
        case None ⇒ throw new IllegalArgumentException("No serializer found for class " + item.getClass)
      }
    }
    ByteBuffers.toByteArray(target)
  }

  def decode(data: Array[Byte]): Seq[Any] = {
    val source = ByteBuffer.wrap(data)
    val buf = new ListBuffer[Any]()
    while (source.hasRemaining) {
      buf += decodeItem(source)
    }
    buf.toSeq
  }

  private def decodeItem[T](source: ByteBuffer): T = {
    val (id, _) = unpack(source.get())
    source.position(source.position() - 1)
    DefaultSerializers.serializerForId(id) match {
      case Some(serializer) ⇒
        val value = serializer.deSerialize(source)
        value.asInstanceOf[T]
      case None ⇒ throw new IllegalArgumentException("No serializer found for serial ID " + id)
    }
  }
}