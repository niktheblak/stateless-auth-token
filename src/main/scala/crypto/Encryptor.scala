package crypto

trait Encryptor {
  def encrypt(data: Array[Byte]): Array[Byte]
  def decrypt(encrypted: Array[Byte]): Array[Byte]
}
