package utils

/**
  * ConfigHelper is an abstraction layer to retrieve neccessary
  * variables for operations within the application. ConfigHelper convers
  * the domains including OpenWhisk, database and etc.
  */
object ConfigHelper {
  /**
    * Username used to interact with OpenWhisk.
    * If WHISK_USER is not set, then uses WHISK_ROOT_USER by default
    * @return
    */
  def WHISK_USER: String = {
    val userName: Option[String] = sys.env.get("WHISK_USER")
    val rootUser: Option[String] = sys.env.get("WHISK_ROOT_USER")

    if (!userName.isEmpty) {
      return userName.get
    } else {
      return rootUser.get
    }
  }

  /**
    * Password used to interact with the OpenWhisk API
    * @return
    */
  def WHISK_PASS: String = {
    val userPass: Option[String] = sys.env.get("WHISK_PASS")
    val rootPass: Option[String] = sys.env.get("WHISK_ROOT_PASS")

    if (!userPass.isEmpty) {
      return userPass.get
    } else {
      return rootPass.get
    }
  }

  /**
    * Gets OpenWhisk host address.
    * It must be localhost during the development
    * @return
    */
  def WHISK_HOST: String = {
    val host = sys.env.get("WHISK_HOST")
    return host.getOrElse("localhost")
  }
}