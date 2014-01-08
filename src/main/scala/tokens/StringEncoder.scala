package tokens

trait StringEncoder {
  def encode(data: Array[Byte]): String
  def decode(data: String): Array[Byte]
}
