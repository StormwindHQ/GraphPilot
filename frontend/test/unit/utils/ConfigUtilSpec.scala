import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import org.mockito.Mockito._
import utils.{ ConfigHelper }

/**
  * Unit tests can run without a full Play application.
  */
/*
class ConfigUtilSpec extends PlaySpec {

  "ConfigHelper" should {

    "return a mocked WHISK_HOST" in {
      // TODO: Fix
      val config: ConfigHelper = new ConfigHelper {
        override def getEnv(name: String): Option[String] = Some("hello.com")
      }
      val host = config.WHISK_HOST
      host must equal("hello.com")
    }

    "return the default WHISK_HOST" in {
      val env = mock[sys.env]
      when(env.get("WHISK_HOST")).thenReturn(None)
      val host = ConfigHelper.WHISK_HOST
      host must equal("localhost")
    }

    "return a mocked WHISK_USER" in {
      val env = mock[sys.env]
      when(env.get("WHISK_USER")).thenReturn(Some("test person"))
      val host = ConfigHelper.WHISK_USER
      print("checking host" + host)
      host must equal("test persoZZn")
    }

  }
}
*/
