package serialization

object VariableLengthIntegerCodec {
  val maxBlocks = 8

  def encode(x: Long): Array[Byte] = {
    require(x >= 0 && x <= 0x80000000000000L)
    val buf = new Array[Byte](8)
    for (i <- 0 to 7) {
      val block = (x >> i * 7) & 0x7F
      val payload = if (i != 0) block | 0x80 else block
      buf(buf.length - i - 1) = payload.toByte
    }
    buf.dropWhile(_ == -128)
  }

  def decode(data: Array[Byte]): Long = {
    var i = 0
    var result = 0L
    while ((data(i) & 0x80) != 0) {
      require(i <= maxBlocks, "Encoded number is too long")
      val payload = data(i) & 0x7F
      result = (result << 7) | payload
      i = i + 1
    }
    result = result | data(i)
    result
  }

  def toBinaryString(arr: Array[Byte]): String = {
    val builder = arr.foldLeft(new StringBuilder) { (builder, b) =>
      val x = b.toInt & 0xFF
      val toPad = Integer.numberOfLeadingZeros(x) - 24
      for (i <- 1 to toPad) builder.append('0')
      builder.append(Integer.toBinaryString(x)).append(' ')
    }
    builder.deleteCharAt(builder.length - 1)
    builder.toString()
  }

  def toBinaryString2(arr: Array[Byte]): String = {
    def padTo8(b: Byte): String = {
      val n = Integer.numberOfLeadingZeros(b) - 24
      ("0" * n) + Integer.toBinaryString(b)
    }
    val str = arr.foldLeft("")((acc, b) => acc + " " + padTo8(b))
    str.tail
  }
}
