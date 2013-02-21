package tokens

case class Authentication(userId: String, role: String, expirationTime: Long)