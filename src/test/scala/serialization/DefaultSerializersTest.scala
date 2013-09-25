package serialization

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import serialization.DefaultSerializers.{LongSerializer, StringSerializer}
import java.nio.ByteBuffer

class DefaultSerializersTest extends FunSpec with ShouldMatchers {
  describe("StringSerializer") {
    it("should serialize strings correctly") {
      val source = "testString"
      val serializer = new StringSerializer
      val encoded = serializer.serialize(source)
      val decoded = serializer.deSerialize(encoded)
      decoded should equal(source)
    }
  }

  describe("LongSerializer") {
    it("should serialize byte-sized numbers efficiently") {
      val serializer = new LongSerializer
      val encoded = serializer.serialize(8)
      encoded.size should equal(2)
      val decoded = serializer.deSerialize(encoded)
      decoded should equal(8)
    }

    it("should serialize word-sized numbers efficiently") {
      val serializer = new LongSerializer
      val encoded = serializer.serialize(800)
      encoded.size should equal(3)
      val decoded = serializer.deSerialize(encoded)
      decoded should equal(800)
    }

    it("should serialize integer-sized numbers efficiently") {
      val serializer = new LongSerializer
      val buf = ByteBuffer.allocate(5)
      val encoded = serializer.serialize(80000)
      encoded.size should equal(5)
      buf.rewind()
      val decoded = serializer.deSerialize(encoded)
      decoded should equal(80000)
    }

    it("should serialize long-sized numbers efficiently") {
      val serializer = new LongSerializer
      val encoded = serializer.serialize(8000000000L)
      encoded.size should equal(9)
      val decoded = serializer.deSerialize(encoded)
      decoded should equal(8000000000L)
    }
  }
}
