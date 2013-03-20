import tokens._

object DefaultTokenEncoder extends TokenCreator {
  val passPhrase = "^YS5Fe>8L@37E513U:69^6*UNY{?"
  val salt = Array[Byte]('S'.toByte, 'e'.toByte, 'r'.toByte, 'v'.toByte, 'e'.toByte, 'r'.toByte, 'N'.toByte, 'a'.toByte)
}