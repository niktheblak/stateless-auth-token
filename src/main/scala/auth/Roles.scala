package auth

object Roles {

  sealed trait Role

  case object Admin extends Role

  case object User extends Role

  def parse(role: String): Option[Role] = {
    role match {
      case "admin" => Some(Admin)
      case "user" => Some(User)
      case _ => None
    }
  }
}
