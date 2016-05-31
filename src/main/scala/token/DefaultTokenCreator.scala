package token

import encoding.FieldEncoderTokenEncoder

trait DefaultTokenCreator
  extends JasyptTokenCreator
  with FieldEncoderTokenEncoder