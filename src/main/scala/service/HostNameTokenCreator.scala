package service

import tokens.{AESSharedKeyEncrypter, FieldEncoderTokenEncoder, ConstantSaltTokenCreator}
import java.net.{UnknownHostException, InetAddress}

trait HostNameTokenCreator extends ConstantSaltTokenCreator with FieldEncoderTokenEncoder with AESSharedKeyEncrypter {
  val salt = generateSaltFromHostName

  private def generateSaltFromHostName =
    try {
      val hostName = InetAddress.getLocalHost.getHostName
      hostName.getBytes("US-ASCII")
    } catch {
      case e: UnknownHostException ⇒ "ServerName".getBytes("US-ASCII")
    }
}
