package services
import javax.inject.Inject
import java.nio.file.{Paths}
import play.api.libs.json._

class Validation {
  def validateTaskInputs(
    appName: String,
    taskType: String,
    taskName: String,
    inputs: JsValue = null,
  ): Unit = {
    val fs = new FileSystem
    val pwd = System.getProperty("user.dir")
    val filePath = Paths.get(pwd, "..", "tasks", appName, taskType, taskName, "schema.json").toString
    val source: String = fs.readFileAsString(filePath)
    val schema: JsValue = Json.parse(source)
    val schemaInputs = (schema \ "inputs").as[List[JsValue]]

    schemaInputs.foreach(x => {
      println(x)
      val name = (x \ "name").as[String]
      val xType = (x \ "type").as[String]
      val required = (x \ "required").as[Boolean]
      println("yo", (inputs \ name))
      // Check required
      if (required && (inputs \ name).isInstanceOf[JsUndefined]) {
        println("undefined!", name)
      }
    })
    true
  }
}