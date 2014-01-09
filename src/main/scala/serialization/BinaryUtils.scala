package serialization

object BinaryUtils {
  def pack(id: Int, size: Int): Int = {
    require(id <= 3, "id must be smaller than 4")
    require(size <= 63, "size must be smaller than 64")
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
