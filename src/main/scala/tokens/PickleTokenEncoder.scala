package tokens

import scala.pickling._
import binary._

import java.nio.ByteBuffer
import java.util.Date

trait PickleTokenEncoder extends TokenEncoder {
  implicit def datePickler(implicit longPickler: SPickler[Long]): SPickler[Date] =
    new SPickler[Date] {
      val format = longPickler.format
      def pickle(date: Date, builder: PBuilder) {
        builder.hintTag(implicitly[FastTypeTag[Date]])
        builder.hintKnownSize(8)
        builder.beginEntry(date)
        builder.putField("millis", b => {
          b.hintTag(FastTypeTag.Long)
          b.hintStaticallyElidedType()
          longPickler.pickle(date.getTime, b)
        })
        builder.endEntry()
      }
    }

  implicit def dateUnpickler(implicit longUnpickler: Unpickler[Long]): Unpickler[Date] =
    new Unpickler[Date] {
      val format = longUnpickler.format
      def unpickle(tag: => FastTypeTag[_], reader: PReader): Any = {
        reader.hintTag(FastTypeTag.Long)
        reader.hintStaticallyElidedType()
        val tag = reader.beginEntry()
        val millis = longUnpickler.unpickle(tag, reader).asInstanceOf[Long]
        reader.endEntry()
        new Date(millis)
      }
    }

  def encodeToken(auth: Authentication, buffer: ByteBuffer) {
    val pickled = auth.pickle
    val data = pickled.value.asInstanceOf[Array[Byte]]
    buffer.put(data)
  }

  def decodeToken(tokenData: ByteBuffer): Authentication = {
    val data = new Array[Byte](tokenData.remaining())
    tokenData.get(data)
    val pickle = BinaryPickle(data)
    pickle.unpickle[Authentication]
  }
}
