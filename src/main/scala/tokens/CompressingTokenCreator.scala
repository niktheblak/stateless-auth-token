package tokens

import java.util.zip.{Inflater, Deflater}

trait CompressingTokenCreator extends JasyptTokenCreator with Base58StringEncoder { self: TokenEncoder ⇒
  override val version = 5
  override val versionBytes = Array[Byte](version.toByte)

  def passPhrase: String
  def salt: Array[Byte]

  override def createTokenString(auth: Authentication): String = {
    val tokenData = createToken(auth)
    val deflated = compress(tokenData)
    encodeString(deflated)
  }

  override def readTokenString(tokenString: String): Authentication = {
    val decoded = decodeString(tokenString)
    val decompressed = decompress(decoded)
    readToken(decompressed)
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
