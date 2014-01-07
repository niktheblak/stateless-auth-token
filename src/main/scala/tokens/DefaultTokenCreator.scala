package tokens

trait DefaultTokenCreator
  extends JasyptTokenCreator
  with FieldEncoderTokenEncoder
  with AESSharedKeyEncrypter