package serialization

import auth.AuthenticationException

class InvalidDataException(message: String, cause: Throwable) extends AuthenticationException(message, cause) {
  def this(message: String) = this(message, null)
  def this(cause: Throwable) = this(null, cause)
  def this() = this(null, null)
}
