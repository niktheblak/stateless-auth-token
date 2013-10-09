package service

import tokens.{FieldEncoderTokenEncoder, TokenCreator}
import java.net.{UnknownHostException, InetAddress}

trait HostNameTokenCreator extends TokenCreator with FieldEncoderTokenEncoder {
  val salt = generateSaltFromHostName

  private def generateSaltFromHostName =
    try {
      val hostName = InetAddress.getLocalHost.getHostName
      hostName.getBytes("US-ASCII")
    } catch {
      case e: UnknownHostException ⇒ "ServerName".getBytes("US-ASCII")
    }
}
