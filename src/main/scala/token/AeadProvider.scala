package token

import com.google.crypto.tink.Aead

trait AeadProvider {
  val aead: Aead
}
