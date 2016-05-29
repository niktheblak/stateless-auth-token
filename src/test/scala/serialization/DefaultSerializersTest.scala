package serialization

import org.scalatest.FunSpec
import org.scalatest.Matchers
import serialization.DefaultSerializers.{ LongSerializer, StringSerializer }
import java.nio.ByteBuffer

class DefaultSerializersTest extends FunSpec with Matchers {
  describe("StringSerializer") {
    it("should serialize strings correctly") {
      val source = "testString"
      val serializer = new StringSerializer
      val encoded = serializer.serialize(source)
      val decoded = serializer.deSerialize(encoded, 0)
      decoded should equal(source)
    }
  }

  describe("LongSerializer") {
    it("should serialize byte-sized numbers efficiently") {
      val serializer = new LongSerializer
      val encoded = serializer.serialize(8)
      encoded should have length 2
      val decoded = serializer.deSerialize(encoded, 0)
      decoded should equal(8)
    }

    it("should serialize word-sized numbers efficiently") {
      val serializer = new LongSerializer
      val encoded = serializer.serialize(800)
      encoded should have length 3
      val decoded = serializer.deSerialize(encoded, 0)
      decoded should equal(800)
    }

    it("should serialize integer-sized numbers efficiently") {
      val serializer = new LongSerializer
      val buf = ByteBuffer.allocate(5)
      val encoded = serializer.serialize(80000)
      encoded should have length 5
      buf.rewind()
      val decoded = serializer.deSerialize(encoded, 0)
      decoded should equal(80000)
    }

    it("should serialize long-sized numbers efficiently") {
      val serializer = new LongSerializer
      val encoded = serializer.serialize(8000000000L)
      encoded should have length 9
      val decoded = serializer.deSerialize(encoded, 0)
      decoded should equal(8000000000L)
    }
  }
}
