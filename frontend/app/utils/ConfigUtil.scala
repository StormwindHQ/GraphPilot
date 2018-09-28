package utils

object ConfigHelper {
  def WHISK_USER: String = {
    val userName: Option[String] = sys.env.get("WHISK_USER")

    // If whisk_user is defined in the env, return that instead of the root user name
    if (!userName.isEmpty) {
      return userName.toString
    } else {
      return sys.env("WHISK_ROOT_USER")
    }
  }

  def WHISK_PASS: String = {
    val userPass: Option[String] = sys.env.get("WHISK_PASS")

    if (!userPass.isEmpty) {
      return userPass.toString
    } else {
      return sys.env("WHISK_ROOT_PASS")
    }
  }
}