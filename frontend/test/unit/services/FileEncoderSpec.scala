import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import org.mockito.Mockito._
import services.FileEncoder

/**
  * Unit tests can run without a full Play application.
  */
class FileEncoderSpec extends PlaySpec {

  "FileEncoder" should {
    "getActionAsBase64" should {

      "return base64 of hello world" in {
        val expected = "aGVsbG8gd29ybGQ=";
        val encoder = new FileEncoder {
          override def readFileAsString(name: String): String = "hello world"
        }
        val result = encoder.getActionAsBase64("github", "actions", "create_issue")
        result must equal(expected)
      }

    }
  }
}
