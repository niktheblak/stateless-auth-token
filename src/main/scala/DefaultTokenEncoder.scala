import java.net.{UnknownHostException, InetAddress}
import tokens._

object DefaultTokenEncoder extends TokenCreator {
  val passPhrase = "^YS5Fe>8L@37E513U:69^6*UNY{?"
  val salt = generateSaltFromHostName

  private def generateSaltFromHostName =
    try {
      val hostName = InetAddress.getLocalHost.getHostName
      hostName.getBytes("US-ASCII")
    } catch {
      case e: UnknownHostException ⇒ "ServerName".getBytes("US-ASCII")
    }
}