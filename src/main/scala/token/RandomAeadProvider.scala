package token

import com.google.crypto.tink.aead.{ AeadConfig, AeadKeyTemplates }
import com.google.crypto.tink.{ Aead, KeysetHandle }

trait RandomAeadProvider extends AeadProvider {
  override val aead: Aead = {
    AeadConfig.register()
    val keysetHandle = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM)
    keysetHandle.getPrimitive(classOf[Aead])
  }
}
