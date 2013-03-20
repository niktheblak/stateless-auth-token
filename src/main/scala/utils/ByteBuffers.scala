package utils

import java.nio.ByteBuffer

object ByteBuffers {
  def toBytes(buf: ByteBuffer): Array[Byte] = {
    val bytes = new Array[Byte](buf.position)
    buf.rewind()
    buf.get(bytes)
    bytes
  }

  def read(buf: ByteBuffer, length: Int): Array[Byte] = {
    require(length > 0)
    val data = new Array[Byte](length)
    buf.get(data)
    data
  }
}
