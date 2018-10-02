import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import org.mockito.Mockito._
import utils.ConfigUtil

/**
  * Unit tests can run without a full Play application.
  */

class ConfigUtilSpec extends PlaySpec {

  "ConfigHelper" should {

    "return a mocked WHISK_HOST" in {
      val config = new ConfigUtil {
        override def getEnv(name: String): Option[String] = Some("hello.com")
      }

      val host = config.WHISK_HOST
      host must equal("hello.com")
    }

    "return the default WHISK_HOST" in {
      val config = new ConfigUtil
      val host = config.WHISK_HOST
      host must equal("localhost")
    }

    "return a mocked WHISK_USER" in {
      val config = new ConfigUtil {
        override def getEnv(name: String): Option[String] = Some("Jason")
      }
      val host = config.WHISK_USER
      host must equal("Jason")
    }

    "return a mocked WHISK_ROOT_USER" in {
      val config = new ConfigUtil {
        // If WHISK_ROOT_USER, return a mock, otherwise return null
        override def getEnv(name: String): Option[String] = {
          if (name == "WHISK_ROOT_USER") {
            Some("RootUser")
          } else {
            null
          }
        }
      }
      val host = config.WHISK_USER
      host must equal("RootUser")
    }

    "return a mocked WHISK_PASS" in {
      val config = new ConfigUtil {
        override def getEnv(name: String): Option[String] = Some("password")
      }
      val host = config.WHISK_PASS
      host must equal("password")
    }

    "return a mocked WHISK_ROOT_PASS" in {
      val config = new ConfigUtil {
        // If WHISK_ROOT_USER, return a mock, otherwise return null
        override def getEnv(name: String): Option[String] = {
          if (name == "WHISK_ROOT_PASS") {
            Some("RootPassword")
          } else {
            null
          }
        }
      }
      val host = config.WHISK_PASS
      host must equal("RootPassword")
    }

  }
}

