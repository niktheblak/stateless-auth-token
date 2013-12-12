package serialization

import org.scalatest.FunSpec
import org.scalatest.Matchers
import BinaryUtils._
import scala.util.Random

class VariableLengthIntegerCodecTest extends FunSpec with Matchers {
  import VariableLengthIntegerCodec._

  describe("VariableLengthIntegerCodec") {
    it("should encode Wikipedia example number correctly") {
      val x = 137
      val arr = encode(x)
      arr should have length 2
      toInt(arr(0)) should equal(129)
      toInt(arr(1)) should equal(9)
    }

    it("should decode Wikipedia example number correctly") {
      val arr = Array(toUnsignedByte(129), toUnsignedByte(9))
      val x = decode(arr, 0)
      x should equal(137)
    }

    it("should encode a two-byte integer correctly") {
      val x = 10292290
      val arr = encode(x)
      arr should have length 4
      toInt(arr(0)) should equal(132)
      toInt(arr(1)) should equal(244)
      toInt(arr(2)) should equal(152)
      toInt(arr(3)) should equal(66)
    }

    it("should roundtrip encode random numbers correctly") {
      val numbers = randomNumbers.take(1000)
      numbers foreach { n ⇒
        decode(encode(n), 0) should equal(n)
      }
    }

    def randomNumbers: Iterator[Int] = new Iterator[Int] {
      val random = new Random
      def hasNext = true
      def next(): Int = random.nextInt(Int.MaxValue)
    }
  }
}
