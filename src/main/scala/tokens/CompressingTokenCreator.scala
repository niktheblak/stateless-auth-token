package tokens

import java.util.zip.{Inflater, Deflater}

trait CompressingTokenCreator extends JasyptTokenCreator with Base58StringEncoder { self: TokenEncoder ⇒
  override val version = 5
  override val versionBytes = Array[Byte](version.toByte)

  def passPhrase: String
  def salt: Array[Byte]

  override def createAuthToken(auth: Authentication): String = {
    val tokenData = createTokenInternal(auth)
    val deflated = compress(tokenData)
    encode(deflated)
  }

  override def decodeAuthToken(tokenString: String): Authentication = {
    val decoded = decode(tokenString)
    val decompressed = decompress(decoded)
    decodeTokenInternal(decompressed)
  }

  def compress(data: Array[Byte]): Array[Byte] = {
    val deflater = new Deflater()
    deflater.setInput(data)
    deflater.finish()
    val deflatedData = new Array[Byte](256)
    val size = deflater.deflate(deflatedData)
    deflatedData.slice(0, size)
  }

  def decompress(data: Array[Byte]): Array[Byte] = {
    val inflater = new Inflater()
    inflater.setInput(data)
    val decompressed = new Array[Byte](256)
    val size = inflater.inflate(decompressed)
    decompressed.slice(0, size)
  }
}
