package tokens

import javax.crypto.{Cipher, SecretKeyFactory}
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec, PBEKeySpec}
import java.nio.ByteBuffer

trait AESSharedKeyEncrypter {
  val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

  def encrypt(data: ByteBuffer, password: Array[Char], salt: Array[Byte], output: ByteBuffer) {
    require(salt.length > 0)
    val spec = new PBEKeySpec(password, salt, 65536, 128)
    val tmp = keyFactory.generateSecret(spec)
    val secret = new SecretKeySpec(tmp.getEncoded, "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secret)
    val params = cipher.getParameters
    val iv = params.getParameterSpec(classOf[IvParameterSpec]).getIV
    assert(iv.length == 16)
    output.put(iv)
    cipher.doFinal(data, output)
  }

  def decrypt(encrypted: ByteBuffer, password: Array[Char], salt: Array[Byte], output: ByteBuffer) {
    val iv = new Array[Byte](16)
    encrypted.get(iv)
    val spec = new PBEKeySpec(password, salt, 65536, 128)
    val tmp = keyFactory.generateSecret(spec)
    val secret = new SecretKeySpec(tmp.getEncoded, "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv))
    cipher.doFinal(encrypted, output)
  }
}
