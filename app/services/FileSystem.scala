package services
import javax.inject._
import sun.misc.{ BASE64Encoder, BASE64Decoder }
import better.files.{File => ScalaFile}
import java.io.{ FileInputStream, FileOutputStream }
import org.apache.commons.codec.binary.Base64
import com.google.common.io.CharStreams
import java.io.{ InputStream, InputStreamReader }
import com.google.common.base.Charsets

/**
  * FileEncoder.scala
  *
  * Context:
  * The service aim to provide easy-to-use file R/W operations throughout Stormwind.io code base.
  * It will have utilities such as getActionAsBase64
  */
class FileSystem () {
  /**
    * Map ScalaFile to File for test mocking
    */
  val File = ScalaFile
  /**
    * Read file as a string
    *
    * @example
    * val fs = new FileSystem
    * fs.readFileAsString("~/tmp/test.txt")
    * // returns a base64 representation of the file
    *
    * @param path
    * @return
    * @sideEffect
    */
  def readFileAsString(path: String): String = {
    // Reading the file as a FileInputStream
    val fileInputStream = new FileInputStream(File(path).toJava)
    // TODO: Is there a way to read as Bytes directly?
    val str = CharStreams.toString(new InputStreamReader(fileInputStream, Charsets.UTF_8))
    return str
  }

  /**
    * Zips an action if a zip file under the action directory doesn't already exist
    * Also you can forcefully zip the task, which deletes already existing ones
    *
    * @example
    * fs.zipDirIfNotExist("github", "triggers", "list_webhooks")
    * // creates a zip file under tasks/github/list_webhooks/list_webhooks.zip
    *
    * @param appName
    * @param taskType
    * @param taskName
    * @param force - Forcefully delete the existing zip file
    */
  def zipTaskIfNotExist(
    appName: String,
    taskType: String,
    taskName: String,
    force: Boolean = true
  ): Boolean = {
    val pwd = System.getProperty("user.dir")
    val dirPath = s"${pwd}/tasks/${appName}/${taskType}/${taskName}"
    val zipPath = s"${dirPath}/${taskName}.zip"
    val dir = File(dirPath)
    val zipFile = File(zipPath)
    if (force && zipFile.exists()) {
      zipFile.delete()
    }

    if (!zipFile.exists()) {
      dir.zipTo(zipFile.path)
    }
    true
  }

  /**
    * Get the zip folder of action as Base64.
    * This is typically need to interact with the OpenWhisk API for creating actions
    * by passing in the base64 representation of the ZIP file.
    *
    * @example
    * val fs = new FileSystem
    * // Execution of the code below return a base64 representation of the task
    * val result = fs.getActionAsBase64("github", "triggers", "list_webhooks")
    *
    * @param appName - application name such as Github
    * @param taskType - taskType can be either actions or triggers
    * @param taskName - name of the task such as create_issue
    * @return
    */
  def getActionAsBase64(
    appName: String,
    taskType: String,
    taskName: String,
    // Use the default method if possible
    readFileAsString: (String) => String = this.readFileAsString
  ): String = {
    val pwd = System.getProperty("user.dir")
    val dirPath = s"${pwd}/tasks/${appName}/${taskType}/${taskName}"
    val zipPath = s"${dirPath}/${taskName}.zip"

    val content: String = readFileAsString(zipPath)
    // Encoding the file using Base64encoder
    val encoded = new BASE64Encoder().encode(content.getBytes(Charsets.UTF_8))
    return encoded.toString
  }
}
