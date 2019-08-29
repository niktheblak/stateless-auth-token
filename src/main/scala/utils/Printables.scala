package utils

object Printables {
  val replacement = '.'

  def getPrintables(data: Array[Byte]): String = {
    val content = data.foldLeft(new StringBuilder) { (builder, b) =>
      if (isPrintable(b)) {
        builder.append(b.toChar)
      } else {
        builder.append(replacement)
      }
      builder
    }
    content.toString()
  }

  def isPrintable(b: Byte): Boolean =
    !Character.isISOControl(b)
}
