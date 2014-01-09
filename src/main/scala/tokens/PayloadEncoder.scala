package tokens

import serialization.InvalidDataException

trait PayloadEncoder { self: TokenEncoder ⇒
  val headerBytes: Array[Byte]
  val versionBytes: Array[Byte]

  def encodePayload(data: Array[Byte]): Array[Byte] = {
    Array.concat(headerBytes, versionBytes, data)
  }
  
  def decodePayload(payload: Array[Byte]): Array[Byte] = {
    val header = payload.slice(0, headerBytes.length)
    checkData(headerBytes, header, "Authentication token header not found")
    val versionStart = headerBytes.size
    val versionEnd = versionStart + versionBytes.size
    val version = payload.slice(versionStart, versionEnd)
    checkData(versionBytes, version, "Unsupported version " + new String(version, "US-ASCII"))
    payload.slice(versionEnd, payload.size)
  }

  def checkLength(expectedLength: Int, actualLength: Int, message: String) {
    if (actualLength < expectedLength) {
      throw new InvalidDataException(message)
    }
  }

  def checkData(expected: Array[Byte], actual: Array[Byte], message: String) {
    if (!java.util.Arrays.equals(expected, actual)) {
      throw new InvalidDataException(message)
    }
  }
}
