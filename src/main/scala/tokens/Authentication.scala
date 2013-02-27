package tokens

import java.util.Date

case class Authentication(userId: String, role: String, expirationTime: Date)