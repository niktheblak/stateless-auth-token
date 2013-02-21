package tokens

import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.PBEKeySpec
import javax.crypto.SecretKeyFactory
import java.nio.ByteBuffer
import java.util.Arrays
import base58.Base58

trait TokenCreator {
  import FieldEncoder.StringSerializer
  val header = "AUTH-TOKEN"
  val version = "1.0"
  val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
  
  val encodingCharset: String
  val passPhrase: String
  val salt: Array[Byte]
  val iterationCount = 3
  
  def headerBytes = header.getBytes(encodingCharset)
  def versionBytes = version.getBytes(encodingCharset)
    
  def createAuthToken(auth: Authentication): String = {
    val tokenData = encodeToken(auth.userId, auth.role, auth.expirationTime)
    val (iv, encrypted) = encrypt(tokenData, salt)
    val tokenWithIv = iv ++ encrypted
    Base58.encode(tokenWithIv)
  }
  
  def decodeAuthToken(tokenString: String): Authentication = {
    val base58 = Base58.decode(tokenString)
    val iv = base58.slice(0, 16)
    val tokenData = base58.slice(16, base58.length)
    val decrypted = decrypt(iv, tokenData, salt)
    decodeToken(decrypted)
  }
  
  def encrypt(data: Array[Byte], salt: Array[Byte]): (Array[Byte], Array[Byte]) = {
    val spec = new PBEKeySpec(passPhrase.toCharArray(), salt, 65536, 128)
    val tmp = keyFactory.generateSecret(spec);
    val secret = new SecretKeySpec(tmp.getEncoded(), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, secret)
    val params = cipher.getParameters()
    val iv = params.getParameterSpec(classOf[IvParameterSpec]).getIV()
    assert(iv.length == 16)
    (iv, cipher.doFinal(data, 0, data.length))
  }
  
  def decrypt(iv: Array[Byte], data: Array[Byte], salt: Array[Byte]): Array[Byte] = {
    require(iv.length == 16)
    val spec = new PBEKeySpec(passPhrase.toCharArray(), salt, 65536, 128)
    val tmp = keyFactory.generateSecret(spec);
    val secret = new SecretKeySpec(tmp.getEncoded(), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv))
    cipher.doFinal(data)
  }
  
  def encodeToken2(userId: String, role: String, expirationTime: Long): Array[Byte] = {
    if (userId.length > Byte.MaxValue) {
      throw new IllegalArgumentException("Too long userId");
    }
    if (role.length > Byte.MaxValue) {
      throw new IllegalArgumentException("Too long role");
    }
    val userIdBytes = userId.getBytes(encodingCharset)
    val userIdLength: Byte = userIdBytes.length.asInstanceOf[Byte]
    val roleBytes = role.getBytes(encodingCharset)
    val roleLength: Byte = role.length.asInstanceOf[Byte]
    val buf = ByteBuffer.allocate(8)
    buf.asLongBuffer().put(expirationTime)
    val payload = Array[Byte](userIdLength) ++ userIdBytes ++ Array[Byte](roleLength) ++ roleBytes ++ buf.array()
    headerBytes ++ versionBytes ++ payload
  }
  
  def encodeToken(userId: String, role: String, expirationTime: Long): Array[Byte] = {
    if (userId.length > Byte.MaxValue) {
      throw new IllegalArgumentException("Too long userId");
    }
    if (role.length > Byte.MaxValue) {
      throw new IllegalArgumentException("Too long role");
    }
    val payload = FieldEncoder.encode(Seq(userId, role, expirationTime.toString))
    headerBytes ++ versionBytes ++ payload
  }
  
  def decodeToken2(tokenData: Array[Byte]): Authentication = {
    val headerData = tokenData.slice(0, headerBytes.length)
    if (!Arrays.equals(headerData, headerBytes)) {
      throw new InvalidDataException("Authentication token header not found");
    }
    val versionData = tokenData.slice(headerBytes.length, headerBytes.length + versionBytes.length)
    val payload = tokenData.slice(headerBytes.length + versionBytes.length, tokenData.length)
    val userIdLength = payload(0)
    val userId = payload.slice(1, userIdLength + 1)
    val roleOffset = userIdLength + 1
    val roleLength = payload(roleOffset)
    val role = payload.slice(roleOffset + 1, roleOffset + roleLength + 1)
    val expirationTimeOffset = roleOffset + roleLength + 1
    val expirationTimeBytes = payload.slice(expirationTimeOffset, payload.length + 1)
    val expirationTime = ByteBuffer.wrap(expirationTimeBytes).asLongBuffer().get()
    Authentication(new String(userId, encodingCharset), new String(role, encodingCharset), expirationTime)
  }
  
  def decodeToken(tokenData: Array[Byte]): Authentication = {
    val header = tokenData.slice(0, headerBytes.length)
    if (!Arrays.equals(header, headerBytes)) {
      throw new InvalidDataException("Authentication token header not found");
    }
    val version = tokenData.slice(headerBytes.length, versionBytes.length)
    val payload = tokenData.slice(headerBytes.length + versionBytes.length, tokenData.length)
    val fields: Seq[String] = FieldEncoder.decode(payload)
    if (fields.length != 3) {
      throw new InvalidDataException("Malformed content");
    }
    val userId = fields(0)
    val role = fields(1)
    val expirationTimeString = fields(2)
    val expirationTime = java.lang.Long.parseLong(expirationTimeString)
    Authentication(userId, role, expirationTime)
  }
}