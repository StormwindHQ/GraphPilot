import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import org.mockito.Mockito._
import services.FileSystem
import scala.reflect.io.Path


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

      val pwd = System.getProperty("user.dir")

      val appName = "github"
      val taskType = "hello"
      val taskName = "zz"

      val testDir = new java.io.File(s"$pwd/tasks/$appName/$taskType/$taskName")

      "create the zip" in {
        // setup: create test dir with files to zip
        val testPath = createTestDirWithFiles(testDir)

        val fs = new FileSystem()

        val result = fs.zipTaskIfNotExist(appName, taskType, taskName)

        val expectedZipPath = testPath.resolve(s"$taskName.zip")

        expectedZipPath.toFile.exists must equal(true)

        // file size
        expectedZipPath.toFile.length().toInt must be > 0

        result must equal(true)

        // teardown
        deleteDir(testPath.getParent.toFile)
      }


      "create the zip when already exists and force=true" in {
        // setup: create test dir already containing zip
        val testPath = createTestDirWithFiles(testDir)
        testPath.resolve(s"$taskName.zip").toFile.createNewFile()

        val fs = new FileSystem()

        fs.zipTaskIfNotExist(appName, taskType, taskName, force=true)

        val expectedZipPath = testPath.resolve(s"$taskName.zip")

        // file size should be Not be zero since the empty zip replaced
        expectedZipPath.toFile.length().toInt must be > 0

        // teardown
        deleteDir(testPath.getParent.toFile)
      }


      "NOT create the zip when already exists and force=false" in {
        // setup: create test dir already containing zip
        val testPath = createTestDirWithFiles(testDir)
        testPath.resolve(s"$taskName.zip").toFile.createNewFile()

        val fs = new FileSystem()

        fs.zipTaskIfNotExist(appName, taskType, taskName, force=false)

        val expectedZipPath = testPath.resolve(s"$taskName.zip")

        // file size should be zero since the empty zip already existed
        expectedZipPath.toFile.length() must equal(0)

        // teardown
        deleteDir(testPath.getParent.toFile)
      }
    }

    def createTestDirWithFiles(testDir: java.io.File): java.nio.file.Path = {
      testDir.mkdirs()
      val testPath = testDir.toPath
      java.nio.file.Files.write(testPath.resolve("test-file-1.txt"), "hello".getBytes)
      java.nio.file.Files.write(testPath.resolve("test-file-2.txt"), "world".getBytes)
      testPath
    }

    def deleteDir(file: java.io.File): Unit = {
      if (file.exists()) {
        Option(file.listFiles()).map(_.foreach(deleteDir(_)))
        file.delete()
      }
    }

  }
}
