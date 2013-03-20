package serialization

trait BitOps {
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
}
