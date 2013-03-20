package serialization

import java.nio.ByteBuffer

trait BinarySerializer[T] {
  def serialize(obj: T, target: ByteBuffer)
  def deSerialize(source: ByteBuffer): T
}
