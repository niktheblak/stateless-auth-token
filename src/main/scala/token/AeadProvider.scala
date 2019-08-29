package token

import com.google.crypto.tink.Aead

trait AeadProvider {
  def getAead: Aead
}
