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
class ConfigUtilSpec extends PlaySpec with MockitoSugar {

  "ConfigHelper" should {

    "return a mocked WHISK_HOST" in {
      val env = sys.env
      when(env.get("WHISK_HOST").getOrElse("localhost")).thenReturn("hello.com")
      val host = ConfigHelper.WHISK_HOST
      host must equal("hello.com")
    }
  }
}
