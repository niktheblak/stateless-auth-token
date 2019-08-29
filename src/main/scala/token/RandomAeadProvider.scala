package token

import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.{ Aead, KeysetHandle }

trait RandomAeadProvider extends AeadProvider {
  def getAead: Aead = {
    val keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM)
    keysetHandle.getPrimitive(classOf[Aead])
  }
}
