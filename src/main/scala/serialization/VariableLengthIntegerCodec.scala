package serialization

import java.nio.ByteBuffer
import utils.ByteBuffers

object VariableLengthIntegerCodec {
  val maxBlocks = 8

  def encode(x: Long): Array[Byte] = {
    require(x >= 0 && x <= Long.MaxValue, s"Argument must be between 0 and ${Long.MaxValue}, inclusive")
    val buf = new Array[Byte](8)
    for (i <- 0 to 7) {
      val block = (x >>> i * 7) & 0x7F
      val payload = if (i != 0) block | 0x80 else block
      buf(buf.length - i - 1) = payload.toByte
    }
    buf.dropWhile(_ == -128)
  }

  def encode(x: Long, target: ByteBuffer): Unit = {
    target.put(encode(x))
  }

  def decode(data: Array[Byte]): Long = {
    if (data.length > maxBlocks) {
      throw new InvalidDataException("Encoded number is too large")
    }
    decode(ByteBuffer.wrap(data))
  }

  def decode(data: Array[Byte], offset: Int): Long = {
    if (data.length > maxBlocks) {
      throw new InvalidDataException("Encoded number is too large")
    }
    decode(ByteBuffer.wrap(data, offset, data.length))
  }

  def decode(buf: ByteBuffer): Long = {
    val iterator = ByteBuffers.toIterator(buf)
    val extendedBytes = iterator.takeWhile(b => (b & 0x80) != 0).take(maxBlocks)
    val result = extendedBytes.foldLeft(0L)((r, b) => {
      val payload = b & 0x7F
      (r << 7) | payload
    })
    val finalByte = iterator.current()
    if ((finalByte & 0x80) != 0) {
      throw new InvalidDataException("Malformed data, encoded number is probably too large")
    }
    (result << 7) | finalByte
  }
}
