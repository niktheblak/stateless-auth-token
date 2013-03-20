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
      val buf = ByteBuffer.allocate(32)
      serializer.serialize(source, buf)
      buf.rewind()
      val decoded = serializer.deSerialize(buf)
      decoded should equal(source)
    }
  }

  describe("LongSerializer") {
    it("should serialize byte-sized numbers efficiently") {
      val serializer = new LongSerializer
      val buf = ByteBuffer.allocate(2)
      serializer.serialize(8, buf)
      buf.position() should equal(2)
      buf.rewind()
      val decoded = serializer.deSerialize(buf)
      decoded should equal(8)
    }

    it("should serialize word-sized numbers efficiently") {
      val serializer = new LongSerializer
      val buf = ByteBuffer.allocate(3)
      serializer.serialize(800, buf)
      buf.position() should equal(3)
      buf.rewind()
      val decoded = serializer.deSerialize(buf)
      decoded should equal(800)
    }

    it("should serialize integer-sized numbers efficiently") {
      val serializer = new LongSerializer
      val buf = ByteBuffer.allocate(5)
      serializer.serialize(80000, buf)
      buf.position() should equal(5)
      buf.rewind()
      val decoded = serializer.deSerialize(buf)
      decoded should equal(80000)
    }

    it("should serialize long-sized numbers efficiently") {
      val serializer = new LongSerializer
      val buf = ByteBuffer.allocate(9)
      serializer.serialize(8000000000L, buf)
      buf.position() should equal(9)
      buf.rewind()
      val decoded = serializer.deSerialize(buf)
      decoded should equal(8000000000L)
    }
  }
}
