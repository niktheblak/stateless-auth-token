package token

import java.nio.charset.Charset

import crypto.RandomSaltGenerator
import encoding.FieldEncoderTokenEncoder

trait DefaultTokenCreator extends TinkTokenCreator with FieldEncoderTokenEncoder with RandomSaltGenerator {
  val header = "AUTH"
  val version = 1
  val encodingCharset: Charset = Charset.forName("UTF-8")
  override val headerBytes: Array[Byte] = header.getBytes(encodingCharset)
  override val versionBytes: Array[Byte] = Array[Byte](version.toByte)
}