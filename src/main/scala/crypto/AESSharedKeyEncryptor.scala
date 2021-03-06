package crypto

import javax.crypto.spec.{ IvParameterSpec, PBEKeySpec, SecretKeySpec }
import javax.crypto.{ Cipher, SecretKeyFactory }

trait AESSharedKeyEncryptor {
  val keyFactory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
  val cipherType = "AES/CBC/PKCS5Padding"
  val keySpecAlgorithm = "AES"
  val iterationCount = 65536
  val keyLength = 256

  def encrypt(data: Array[Byte], password: Array[Char], salt: Array[Byte]): Array[Byte] = {
    require(salt.length > 0)
    val keySpec = new PBEKeySpec(password, salt, iterationCount, keyLength)
    val tmp = keyFactory.generateSecret(keySpec)
    val secret = new SecretKeySpec(tmp.getEncoded, keySpecAlgorithm)
    val cipher = Cipher.getInstance(cipherType)
    cipher.init(Cipher.ENCRYPT_MODE, secret)
    val params = cipher.getParameters
    val iv = params.getParameterSpec(classOf[IvParameterSpec]).getIV
    assert(iv.length == 16)
    iv ++ cipher.doFinal(data)
  }

  def decrypt(encrypted: Array[Byte], password: Array[Char], salt: Array[Byte]): Array[Byte] = {
    require(encrypted.length > 16)
    val iv = encrypted.slice(0, 16)
    val spec = new PBEKeySpec(password, salt, iterationCount, keyLength)
    val tmp = keyFactory.generateSecret(spec)
    val secret = new SecretKeySpec(tmp.getEncoded, keySpecAlgorithm)
    val cipher = Cipher.getInstance(cipherType)
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv))
    val payload = encrypted.drop(16)
    cipher.doFinal(payload)
  }
}
