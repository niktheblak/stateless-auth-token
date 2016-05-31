package crypto

trait Encryptor {
  def encrypt(data: Array[Byte], salt: Array[Byte]): Array[Byte]
  def decrypt(encrypted: Array[Byte], salt: Array[Byte]): Array[Byte]
}
