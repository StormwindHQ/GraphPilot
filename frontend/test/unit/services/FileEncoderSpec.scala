import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

import java.io.{File, FileInputStream, FileOutputStream}

import services.FileEncoder

/**
  * Unit tests can run without a full Play application.
  */
class FileEncoderSpec extends PlaySpec {
  "FileEncoder" should {
    "should pass" in {
      val encoder = new FileEncoder
      val in = mock[FileInputStream]
      // TODO: Finish this
      1 must equal(1)
    }
  }
}
