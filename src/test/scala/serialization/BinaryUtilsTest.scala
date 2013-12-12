package serialization

import org.scalatest.Matchers
import org.scalatest.FlatSpec

class BinaryUtilsTest extends FlatSpec with Matchers {
  import BinaryUtils._

  "BinaryUtils" should "pack two bytes correctly" in {
    val packed = pack(2, 0x2B)
    packed should equal(0xAB)
  }

  it should "unpack two bytes correctly" in {
    val (id, size) = unpack(0xAB)
    id should equal(2)
    size should equal(0x2B)
  }
}
