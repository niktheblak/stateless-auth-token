package auth

object Roles {
  sealed trait Role
  case object Admin extends Role
  case object User extends Role
}
