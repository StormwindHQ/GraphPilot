package services
import javax.inject.Inject
import java.nio.file.Paths
import play.api.libs.json.{ JsValue, Json, JsUndefined }
import scala.xml.dtd.ValidationException

class Validation {

  /**
    * Given a JsValue payload, and a schema.json to pick up;
    * It will compare the payoad and process validation accordingly
    * 1. Check required = if schema.required == true, then payload["name"] should not be undefined
    * @param appName
    * @param taskType
    * @param taskName
    * @param inputs
    * @return
    */
  def validateTaskPayload(
    appName: String,
    taskType: String,
    taskName: String,
    payload: JsValue, // can be either env or inputs
    payloadType: String = "schema",
  ): Boolean = {
    val fs = new FileSystem
    val pwd = System.getProperty("user.dir")
    val filePath = Paths.get(pwd, "..", "tasks", appName, taskType, taskName, s"${payloadType}.json").toString
    val source: String = fs.readFileAsString(filePath)
    val schema: JsValue = Json.parse(source)
    val schemaInputs = (schema \ "inputs").as[List[JsValue]]

    schemaInputs.foreach(x => {
      // Loop to check values of the given inputs
      val name = (x \ "name").as[String]
      val xType = (x \ "type").as[String]
      val required = (x \ "required").as[Boolean]

      // 1. If the current target is required, inputs should have the value
      if (required && (inputs \ name).isInstanceOf[JsUndefined]) {
        throw new ValidationException(f"${name} is a required value")
      }

      // 2.
    })
    true
  }
}