package serialization

object BitOps {
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

  def padWith(c: Char, n: Int)(str: String): String = {
    if (str.length < n) {
      val amountToPad = n - str.length
      (c.toString * amountToPad) + str
    } else str
  }

  def toBinaryString(b: Byte): String =
    padWith('0', 8)(Integer.toBinaryString(toInt(b)))

  def toGroupedBinaryString(n: Int): String =
    Integer.toBinaryString(n).grouped(8).map(padWith('0', 8)).mkString(" ")

  def toBinaryString(arr: Array[Byte]): String = {
    val builder = arr.foldLeft(new StringBuilder) { (builder, b) ⇒
      val x = toInt(b)
      val toPad = Integer.numberOfLeadingZeros(x) - 24
      for (i ← 1 to toPad) builder.append('0')
      builder.append(Integer.toBinaryString(x)).append(' ')
    }
    builder.deleteCharAt(builder.length - 1)
    builder.toString()
  }

  def toBinaryString2(arr: Array[Byte]): String = {
    val str = arr.foldLeft("")((acc, b) ⇒ acc + " " + toBinaryString(b))
    str.tail
  }
}
