package serialization

import java.nio.ByteBuffer

object VariableLengthIntegerCodec {
  val maxBlocks = 8

  def encode(x: Long): Array[Byte] = {
    require(x >= 0 && x <= 0x80000000000000L)
    val buf = new Array[Byte](8)
    for (i ← 0 to 7) {
      val block = (x >>> i * 7) & 0x7F
      val payload = if (i != 0) block | 0x80 else block
      buf(buf.length - i - 1) = payload.toByte
    }
    buf.dropWhile(_ == -128)
  }

  def encode(x: Long, target: ByteBuffer) {
    target.put(encode(x))
  }

  def decode(data: Array[Byte]): Long = {
    require(data.length <= maxBlocks, "Encoded number is too long")
    var result = 0L
    for (i <- 0 until data.length) {
      val payload = data(i) & 0x7F
      result = (result << 7) | payload
    }
    result
  }

  def decode(data: Array[Byte], offset: Int): Long = {
    var i = offset
    var result = 0L
    while (i < data.length && (data(i) & 0x80) != 0) {
      require(i <= maxBlocks, "Encoded number is too long")
      val payload = data(i) & 0x7F
      result = (result << 7) | payload
      i = i + 1
    }
    require((data(i) & 0x80) == 0)
    result = (result << 7) | data(i)
    result
  }

  def decode(data: ByteBuffer): Long = {
    var result = 0L
    var i = 0
    var b = data.get()
    while ((b & 0x80) != 0) {
      require(i <= maxBlocks, "Encoded number is too long")
      val payload = b & 0x7F
      result = (result << 7) | payload
      i = i + 1
      b = data.get()
    }
    require((b & 0x80) == 0)
    result = (result << 7) | b
    result
  }
}
