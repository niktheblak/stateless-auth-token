package tokens

import javax.crypto.{ Cipher, SecretKeyFactory }
import javax.crypto.spec.{ IvParameterSpec, SecretKeySpec, PBEKeySpec }

trait AESSharedKeyEncrypter extends Encrypter {
  val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
  val cipherType = "AES/CBC/PKCS5Padding"
  val keySpecAlgorithm = "AES"

  def encrypt(data: Array[Byte], password: Array[Char], salt: Array[Byte]): Array[Byte] = {
    require(salt.length > 0)
    val spec = new PBEKeySpec(password, salt, 65536, 128)
    val tmp = keyFactory.generateSecret(spec)
    val secret = new SecretKeySpec(tmp.getEncoded, keySpecAlgorithm)
    val cipher = Cipher.getInstance(cipherType)
    cipher.init(Cipher.ENCRYPT_MODE, secret)
    val params = cipher.getParameters
    val iv = params.getParameterSpec(classOf[IvParameterSpec]).getIV
    assert(iv.length == 16)
    iv ++ cipher.doFinal(data)
  }

  def decrypt(encrypted: Array[Byte], password: Array[Char], salt: Array[Byte]): Array[Byte] = {
    val iv = encrypted.slice(0, 16)
    val spec = new PBEKeySpec(password, salt, 65536, 128)
    val tmp = keyFactory.generateSecret(spec)
    val secret = new SecretKeySpec(tmp.getEncoded, keySpecAlgorithm)
    val cipher = Cipher.getInstance(cipherType)
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv))
    val payload = encrypted.slice(16, encrypted.length)
    cipher.doFinal(payload)
  }
}
