package serialization

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

class VariableLengthIntegerCodecTest extends FunSpec with ShouldMatchers {
  describe("VariableLengthIntegerCodec") {
    it("should encode Wikipedia example number correctly") {
      val x = 137
      val arr = VariableLengthIntegerCodec.encode(x)
      arr should have length 2
      arr(0) should equal(-127)
      arr(1) should equal(9)
    }

    /*it("should encode a two-byte integer correctly") {
      val x = 0xCF3L
      val arr = VariableLengthIntegerCodec.encode(x)
      arr should have length 2
      arr(0) should equal(0x19)
      arr(1) should equal(0x73)
    }

    it("should decode a two-byte integer correctly") {
      val arr = Array(0x19.toByte, 0x73.toByte)
      val x = VariableLengthIntegerCodec.decode(arr)
      x should equal(0xCF3L)
    }*/
  }
}
