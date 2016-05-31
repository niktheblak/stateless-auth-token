package encoding

trait StringEncoder {
  def encodeString(data: Array[Byte]): String
  def decodeString(data: String): Array[Byte]
}
