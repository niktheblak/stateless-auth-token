package utils

import java.nio.ByteBuffer

object ByteBuffers {
  def toByteArray(buf: ByteBuffer): Array[Byte] = {
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
  
  trait RetainingIterator[+A] extends Iterator[A] {
    def current(): A
  }

  def toIterator(buf: ByteBuffer): RetainingIterator[Byte] = new RetainingIterator[Byte] {
    private var _current: Byte = -1
    
    override def current(): Byte = _current
    
    override def next(): Byte = {
      _current = buf.get()
      _current
    }

    override def hasNext: Boolean = buf.hasRemaining
  }
}
