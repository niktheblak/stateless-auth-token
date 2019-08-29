package encoding

import serialization.InvalidDataException
import utils.Printables

trait PayloadEncoder { self: TokenEncoder =>
  val headerBytes: Array[Byte]
  val versionBytes: Array[Byte]

  def encodePayload(data: Array[Byte]): Array[Byte] = {
    Array.concat(headerBytes, versionBytes, data)
  }

  def decodePayload(payload: Array[Byte]): Array[Byte] = {
    val header = payload.slice(0, headerBytes.length)
    checkData(headerBytes, header, s"Invalid header: ${Printables.getPrintables(header)}")
    val versionStart = headerBytes.length
    val versionEnd = versionStart + versionBytes.length
    val version = payload.slice(versionStart, versionEnd)
    checkData(versionBytes, version, s"Unsupported version: ${Printables.getPrintables(version)}")
    payload.slice(versionEnd, payload.length)
  }

  def checkLength(expectedLength: Int, actualLength: Int, message: String): Unit = {
    if (actualLength < expectedLength) {
      throw new InvalidDataException(message)
    }
  }

  def checkData(expected: Array[Byte], actual: Array[Byte], message: String): Unit = {
    if (!java.util.Arrays.equals(expected, actual)) {
      throw new InvalidDataException(message)
    }
  }
}
