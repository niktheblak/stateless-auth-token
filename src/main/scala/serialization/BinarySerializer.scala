package serialization

trait BinarySerializer[T] {
  def serialize(obj: T): Array[Byte]
  def deSerialize(source: Array[Byte], offset: Int): T
}
