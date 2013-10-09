package tokens

trait Encrypter {
  def encrypt(data: Array[Byte], password: Array[Char], salt: Array[Byte]): Array[Byte]
  def decrypt(encrypted: Array[Byte], password: Array[Char], salt: Array[Byte]): Array[Byte]
}
