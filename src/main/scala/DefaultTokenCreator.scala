import tokens.{RandomSaltTokenCreator, AESSharedKeyEncrypter, FieldEncoderTokenEncoder}

object DefaultTokenCreator extends RandomSaltTokenCreator with FieldEncoderTokenEncoder with AESSharedKeyEncrypter {
  val passPhrase = "^YS5Fe>8L@37E513U:69^6*UNY{?"
}