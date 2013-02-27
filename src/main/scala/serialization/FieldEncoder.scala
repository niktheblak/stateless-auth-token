package serialization

import scala.collection.mutable.ListBuffer
import java.nio.ByteBuffer

object FieldEncoder extends BitOps {
  def encode[T](items: Seq[T], target: ByteBuffer) {
    items foreach { item =>
      DefaultSerializers.serializerFor(item) match {
        case Some(serializer) => serializer.serialize(item, target)
        case None => throw new IllegalArgumentException("No serializer found for class " + item.getClass)
      }
    }
  }

  def decode(source: ByteBuffer): Seq[Any] = {
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
      case Some(serializer) =>
        val value = serializer.deSerialize(source)
        value.asInstanceOf[T]
      case None => throw new IllegalArgumentException("No serializer found for serial ID " + id)
    }
  }
}