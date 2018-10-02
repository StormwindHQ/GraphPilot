package services
import javax.inject._
import sun.misc.{BASE64Encoder, BASE64Decoder}
import better.files._
import java.io.{File => JFile, FileInputStream, FileOutputStream}
import java.nio.file.{Paths, Files => JFiles}
import com.google.common.io.{Files => GFiles}
import org.apache.commons.codec.binary.Base64
import com.google.common.io.CharStreams
import java.io.{InputStream, InputStreamReader}
import com.google.common.base.Charsets

/**
  * FileEncoder.scala
  *
  * Context:
  * The service aim to provide easy-to-use file R/W operations throughout Stormwind.io code base.
  * It will have utilities such as getActionAsBase64
  */
class FileSystem {

  /**
    * Read file as a string
    * @param path
    * @return
    * @sideEffect
    */
  def readFileAsString(path: String): String = {
    // Reading the file as a FileInputStream
    val fileInputStream = new FileInputStream(new JFile(path))
    // TODO: Is there a way to read as Bytes directly?
    val str = CharStreams.toString(new InputStreamReader(fileInputStream, Charsets.UTF_8))
    return str
  }

  /**
    * Zips an action if a zip file under the action directory doesn't already exist
    * @param dirPath - directory to zip
    * @param zipPath - zip path to check if it exist, otherwise create a zip of dirPath
    */
  def zipDirIfNotExist(
    dirPath: String,
    zipPath: String,
  ): Unit = {
    val pwd = System.getProperty("user.dir")
    if (!JFiles.exists(Paths.get(zipPath))) {
      File(dirPath).zipTo(File(zipPath))
    }
  }

  /**
    * Get the zip folder of action as Base64.
    * This is typically need to interact with the OpenWhisk API for creating actions
    * by passing in the base64 representation of the ZIP file.
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
    val taskPath = Paths.get(pwd, "tasks", appName, taskType, taskName)
    val filePath = Paths.get(taskPath.toString, taskName + ".zip")

    // Zips the task if it's not already done yet
    this.zipDirIfNotExist(
      dirPath=taskPath.toString,
      zipPath=filePath.toString,
    )
    val content: String = readFileAsString(filePath.toString)
    // Encoding the file using Base64encoder
    val encoded = new BASE64Encoder().encode(content.getBytes(Charsets.UTF_8))
    return encoded.toString
  }
}
