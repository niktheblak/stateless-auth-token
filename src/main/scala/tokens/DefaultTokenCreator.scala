package tokens

trait DefaultTokenCreator
  extends RandomSaltTokenCreator
  with FieldEncoderTokenEncoder
  with AESSharedKeyEncrypter