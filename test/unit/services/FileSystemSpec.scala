import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import org.mockito.Mockito._
import services.FileSystem

/**
  * Unit tests can run without a full Play application.
  */
class FileSystemSpec extends PlaySpec {

  "FileSystem" should {
    "getActionAsBase64" should {

      "return base64 of hello world" in {
        val expected = "aGVsbG8gd29ybGQ=";
        val fs = new FileSystem {
          override def readFileAsString(name: String): String = "hello world"
        }
        val result = fs.getActionAsBase64("github", "actions", "create_issue")
        result must equal(expected)
      }

      "should return an empty result for empty string input" in {
        val expected = "";
        val fs = new FileSystem {
          override def readFileAsString(name: String): String = ""
        }
        val result = fs.getActionAsBase64("github", "actions", "create_issue")
        result must equal(expected)
      }

    }

    "zipTaskIfNotExist" should {
      "return true without errors" in {

        /* val fs = new FileSystem {
          override val File = FakeFile
        }

        val result = fs.zipTaskIfNotExist("github", "hello", "zz") */
        // TODO: Finish implementing it
        true must equal(true)
      }
    }

  }
}
