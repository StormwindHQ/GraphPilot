package services
import com.google.common.io.CharStreams
import javax.inject._
import sun.misc.{ BASE64Encoder, BASE64Decoder }
import better.files.{File => ScalaFile}
import org.apache.commons.codec.binary.Base64
import scala.io.BufferedSource
import scala.io.Codec
import java.io.{
  File => JFile, InputStreamReader,
  FileInputStream, FileOutputStream, InputStream,
  IOException, OutputStream
}
import java.util.zip.{ZipEntry, ZipFile, ZipOutputStream}
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
    * // creates a zip file under tasks/github/triggers/list_webhooks/list_webhooks.zip
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
      val zipUtil = new ZipArchiveUtil
      val jDir = dir.toJava
      val filePaths = zipUtil.createFileList(jDir, zipPath)
      zipUtil.createZip(filePaths, zipPath, dirPath)
      // dir.zipTo(zipFile.path) // bug reported https://github.com/pathikrit/better-files/issues/268
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

/**
  * Util Script to zip an archive
  * Original Source: https://github.com/dhbikoff/Scala-Zip-Archive-Util/blob/master/ZipArchiveUtil.scala
  * @example
  * val zipUtil = new ZipArchiveUtil
  * val dirToZip = File("/home/jason/tmp/hello")
  * val zipPath = s"{dirToZip.path}/hello.zip"
  * val filePaths = zipUtil.createFileList(dirToZip, zipPath)
  * zipUtil.createZip(filePaths, zipPath, dirToZip.path)
  *
  */
class ZipArchiveUtil {
  def createFileList(file: JFile, outputFilename: String): List[String] = {
    file match {
      case file if file.isFile => {
        if (file.getName != outputFilename)
          List(file.getAbsoluteFile.toString)
        else
          List()
      }
      case file if file.isDirectory => {
        val fList = file.list
        // Add all files in current dir to list and recur on subdirs
        fList.foldLeft(List[String]())((pList: List[String], path: String) =>
          pList ++ createFileList(new JFile(file, path), outputFilename))
      }
      case _ => throw new IOException("Bad path. No file or directory found.")
    }
  }

  def addFileToZipEntry(filename: String, parentPath: String,
                        filePathsCount: Int): ZipEntry = {
    if (filePathsCount <= 1)
      new ZipEntry(new JFile(filename).getName)
    else {
      // use relative path to avoid adding absolute path directories
      val relative = new JFile(parentPath).toURI.
        relativize(new JFile(filename).toURI).getPath
      new ZipEntry(relative)
    }
  }

  def createZip(filePaths: List[String], outputFilename: String,
                parentPath: String) = {
    try {
      val fileOutputStream = new FileOutputStream(outputFilename)
      val zipOutputStream = new ZipOutputStream(fileOutputStream)

      filePaths.foreach((name: String) => {
        val zipEntry = addFileToZipEntry(name, parentPath, filePaths.size)
        zipOutputStream.putNextEntry(zipEntry)
        val inputSrc = new BufferedSource(
          new FileInputStream(name))(Codec.ISO8859)
        inputSrc foreach { c: Char => zipOutputStream.write(c) }
        inputSrc.close
      })

      zipOutputStream.closeEntry
      zipOutputStream.close
      fileOutputStream.close

    } catch {
      case e: IOException => {
        e.printStackTrace
      }
    }
  }

}
