package serialization

object BinaryUtils {
  val maxIdLength = 3
  val maxSizeLength = 63

  def pack(id: Int, size: Int): Int = {
    require(id <= maxIdLength, s"id must be less than $maxIdLength")
    require(size <= maxSizeLength, s"size must be less than $maxSizeLength")
    val packed = ((id & 0x3) << 6) | (size & 0x3F)
    packed
  }

  def unpack(x: Int): (Int, Int) = {
    val id = x >> 6
    val size = x & 0x3F
    (id, size)
  }

  def toInt(b0: Byte, b1: Byte): Int = {
    (b0 & 0xFF) << 8 | (b1 & 0xFF)
  }

  def toInt(b: Byte): Int =
    b.toInt & 0xFF

  def toUnsignedByte(n: Int): Byte = (n & 0xFF).toByte
}
