package services
import javax.inject._
import java.io.{File, FileInputStream, FileOutputStream}
import sun.misc.{BASE64Encoder, BASE64Decoder}
import java.nio.file.{Paths}
import com.google.common.io.Files
import com.google.common.base.Charsets
import org.apache.commons.codec.binary.Base64
import com.google.common.io.CharStreams
import java.io.{InputStream, InputStreamReader}

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
    val fileInputStream = new FileInputStream(new File(path))
    // TODO: Is there a way to read as Bytes directly?
    val str = CharStreams.toString(new InputStreamReader(fileInputStream, Charsets.UTF_8))
    return str
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
    val filePath = Paths.get(pwd, "..", "tasks", appName, taskType, taskName, taskName + ".zip").toString
    val simplified = Files.simplifyPath(filePath)
    val content: String = readFileAsString(simplified)
    // Encoding the file using Base64encoder
    val encoded = new BASE64Encoder().encode(content.getBytes(Charsets.UTF_8))
    return encoded.toString
  }
}
