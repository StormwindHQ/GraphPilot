package services
import javax.inject._
import java.io.{File, FileInputStream, FileOutputStream}
import sun.misc.{BASE64Encoder, BASE64Decoder}
import java.nio.file.{Paths}
import com.google.common.io.Files
import org.apache.commons.codec.binary.Base64

/**
  * FileEncoder.scala
  *
  * Context:
  * The service aim to provide easy-to-use file R/W operations throughout Stormwind.io code base.
  * It will have utilities such as getActionAsBase64
  */
@Singleton
class FileEncoder {

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
    appName: String = null,
    taskType: String = null,
    taskName: String = null
  ): String = {
    val pwd = System.getProperty("user.dir")
    val filePath = Paths.get(pwd, "..", "tasks", appName, taskType, taskName, taskName + ".zip").toString
    val simplified = Files.simplifyPath(filePath)

    // Reading the file as a FileInputStream
    val file = new File(simplified)
    val in = new FileInputStream(file)
    val bytes = new Array[Byte](file.length.toInt)
    in.read(bytes) // stream inserts bytes into the array
    in.close()

    // Encoding the file using Base64encoder
    val encoded =
      new BASE64Encoder()
        .encode(bytes)
        .replace("\n", "")
        .replace("\r", "")
    return encoded.toString
  }
}