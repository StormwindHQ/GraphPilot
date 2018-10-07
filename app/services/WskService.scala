package services
import javax.inject._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import scala.concurrent.Future
import play.api.libs.ws._
import play.api.http.HttpEntity
import play.api.libs.json._
import services.{ Validation }
import utils.ConfigUtil

/**
  * List of available environment types in the latest OpenWhisk
  */
object TaskKind extends Enumeration {
  val php7 = Value("php:7.1")
  val swift4 = Value("swift:4.1")
  val node8 = Value("nodejs:8")
  val nodejs = Value("nodejs")
  val blackbox = Value("blackbox")
  val java = Value("java")
  val sequence = Value("sequence")
  val node6 = Value("nodejs:6")
  val python3 = Value("python:3")
  val python = Value("python")
  val python2 = Value("python:2")
  val swift3 = Value("swift:3.1.1")
}

/**
  * WskService.scala
  *
  * Context:
  * The class provides interface to OpenWhisk backend. Username and password are
  * mandatory security requirements and must be provided in order to operate this class.
  * Naming convention for each utility method follows:
  * [R] get... (if querying a single)
  * [R] list... (if querying a multiple)
  * [W] create..
  * [U] update..
  * [D] delete..
  *
  * @param ws
  * @param fs
  * @param executionContext
  */
@Singleton
class WskService @Inject() (
  ws: WSClient,
  fs: FileSystem,
  config: ConfigUtil,
)(implicit executionContext: ExecutionContext) {
  /**
    * Get available name spaces in the OpenWhisk instance
    * @return Future<String>
    */
  def listNamespaces(): Future[String] = {
    // TODO: String interpolation + abstracted value

    ws.url(s"https://${config.WHISK_HOST}/api/v1/namespaces")
      .withAuth(config.WHISK_USER, config.WHISK_PASS, WSAuthScheme.BASIC)
      .get()
      .map { response => response.body }
  }

  /**
    * Create an action using the OpenWhisk REST API.
    * It base64 encodes the zip file of an action and
    * post it to OpenWhisk using PUT method to create the action.
    * Note that the function is called createTask but it's technically
    * creating an Action from OpenWhisk's end
    * @param appName
    * @param taskType
    * @param taskName
    * @param kind
    * @return
    */
  def createTask(
    appName: String,
    taskType: String,
    taskName: String,
    kind: TaskKind.Value,
    inputs: JsValue,
    // env: JsObject
  ): Future[String] = {
    // Validate the inputs
    val validator = new Validation()
   //  validator.validateTaskPayload(
    //  appName, taskType, taskName, inputs)
    // Zips the task if it's not zipped already
    fs.zipTaskIfNotExist(
      appName, taskType, taskName, true)
    val encodedAction = fs.getActionAsBase64(appName, taskType, taskName)
    val body: JsValue = JsObject(Seq(
      "exec" -> JsObject(Seq(
        "kind" -> JsString(kind.toString),
        "code" -> JsString(encodedAction)
      ))
    ))
    ws.url(s"https://${config.WHISK_HOST}/api/v1/namespaces/guest/actions/hello")
      .withHttpHeaders("Accept" -> "application/json")
      .withAuth(config.WHISK_USER, config.WHISK_PASS, WSAuthScheme.BASIC)
      .put(body)
      .map { response => response.body }
  }

  /**
    * Returns a list of tasks in the current OpenWhisk instance in a string format
    * @return
    */
  def listTasks(): Future[String] = {
    ws.url(s"https://${config.WHISK_HOST}/api/v1/namespaces/guest/actions")
      .withAuth(config.WHISK_USER, config.WHISK_PASS, WSAuthScheme.BASIC)
      .get()
      .map { response => response.body }
  }
}
