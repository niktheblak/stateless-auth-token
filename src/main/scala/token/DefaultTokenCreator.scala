package token

import java.nio.charset.Charset

import encoding.FieldEncoderTokenEncoder

trait DefaultTokenCreator  extends JasyptTokenCreator with FieldEncoderTokenEncoder {
  val header = "AUTH"
  val version = 3
  val encodingCharset = Charset.forName("UTF-8")
  override val headerBytes = header.getBytes(encodingCharset)
  override val versionBytes = Array[Byte](version.toByte)
}