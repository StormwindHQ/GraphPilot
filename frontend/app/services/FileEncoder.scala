package services
import javax.inject._
import java.io.{File, FileInputStream, FileOutputStream}
import sun.misc.{BASE64Encoder, BASE64Decoder}
import java.nio.file.{Paths}
import com.google.common.io.Files

/**
  * FileEncoder.scala
  *
  * Context:
  * The service aim to provide easy-to-use file R/W operations throughout Stormwind.io code base.
  * It will have utilities such as getActionAsBase64
  */
@Singleton
class FileEncoder {
  def getActionAsBase64(appName: String = null, taskType: String = null, taskName: String = null): Unit = {
    // It should be (pathToTheProject)/frontend
    val pwd = System.getProperty("user.dir")
    val filePath = Paths.get(pwd, "..", "tasks", appName, taskType, taskName, taskName + ".zip").toString
    val file = new File(filePath).toString
    val simplified = Files.simplifyPath(file)
    val in = new FileInputStream(simplified)
    val bytes = new Array[Byte](file.length.toInt)
    in.read(bytes)
    in.close()
    print("hey!!")
    print("checking file" + bytes)
  }
}