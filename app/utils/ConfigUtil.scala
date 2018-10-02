package utils
/**
  * ConfigHelper is an abstraction layer to retrieve neccessary
  * variables for operations within the application. ConfigUtil convers
  * the domains including OpenWhisk, database and etc.
  */
class ConfigUtil {
  /**
    * sys.env.get simple wrapper
    * @param name
    * @return
    */
  def getEnv(name: String) = {
    sys.env.get(name)
  }
  /**
    * Username used to interact with OpenWhisk.
    * If WHISK_USER is not set, then uses WHISK_ROOT_USER by default
    * @return
    */
  def WHISK_USER: String = {
    val userName: Option[String] = getEnv("WHISK_USER")
    val rootUser: Option[String] = getEnv("WHISK_ROOT_USER")
    if (userName != null && !userName.isEmpty) {
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
    val userPass: Option[String] = getEnv("WHISK_PASS")
    val rootPass: Option[String] = getEnv("WHISK_ROOT_PASS")

    if (userPass != null && !userPass.isEmpty) {
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
    val host = getEnv("WHISK_HOST")
    return host.getOrElse("localhost")
  }
}
