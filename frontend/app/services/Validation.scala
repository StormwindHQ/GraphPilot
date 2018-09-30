package services
import javax.inject.Inject
import java.nio.file.Paths
import play.api.libs.json._
import scala.xml.dtd.ValidationException

import consts.SchemaConst.{ TYPE_STRING }
import consts.ErrorMessages.{ MSG_REQUIRED, MSG_NOT_STRING }

/**
  * A universal validation provider. It should be split out in the future.
  */
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
      val value = (payload \ name)
      // 1. If the current target is required, inputs should have the value
      if (required && value.isInstanceOf[JsUndefined]) {
        throw new ValidationException(MSG_REQUIRED.format(name))
      }

      // 2. Check string type
      if (xType == TYPE_STRING && !value.isInstanceOf[JsString]) {
        throw new ValidationException(MSG_NOT_STRING.format(name))
      }
    })
    true
  }
}