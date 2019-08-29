package auth

import java.time.Instant

case class Authentication(userId: String, role: Roles.Role, expirationTime: Instant)