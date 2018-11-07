package services
import javax.inject._
import scala.concurrent.ExecutionContext

import scala.concurrent.{ Future, Await }
import scala.util.{ Success, Failure }
import play.api.libs.ws._
import play.api.http.HttpEntity
import play.api.libs.json._
import utils.ConfigUtil
import scala.concurrent.duration._

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

trait FaaS {
  /**
    * Lists the available namespaces. It may not be available for
    * all FaaS services.
    * @return
    */
  def listNamespaces(): Future[String]

  /**
    * Creates a task according to the below arguments
    * @param id
    * @param appName
    * @param taskType
    * @param taskName
    * @param kind
    * @param inputs
    * @return
    */
  def createTask(
    id: String,
    appName: String,
    taskType: String,
    taskName: String,
    kind: TaskKind.Value,
    inputs: JsValue
  ): Future[String]

  /**
    * Create an OpenWhisk sequence
    * @param seqId - An unique ID for the sequence
    * @param taskIds - A list of task IDs
    * @return
    */
  def createSequence(seqId: String, taskIds: List[String]): Future[String]

  /**
    * Returns a list of tasks
    * @return
    */
  def listTasks(): Future[String]

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
)(implicit executionContext: ExecutionContext) extends FaaS {
  /**
    * Get available name spaces in the OpenWhisk instance
    * @return Future<String>
    */
  override def listNamespaces(): Future[String] = {
    // TODO: abstract api, v1, namespaces
    ws.url(s"https://${config.WHISK_HOST}/api/v1/namespaces")
      .withAuth(config.WHISK_USER, config.WHISK_PASS, WSAuthScheme.BASIC)
      .get()
      .map { response => response.body }
  }

  /**
    * Create an action using the OpenWhisk REST API.
    * It base64 encodes the zip file of an action and
    * post it to OpenWhisk using PUT method to create the action.
    *
    * @example
    * createTask(
    *   id="test-1-",
    *   appName="github",
    *   taskType="actions",
    *   taskName="create_issue",
    *   kind=TaskKind.node6,
    *   inputs=<someInputs>
    * ).map(taskId => println("successfully created a task with an ID:", taskId))
    *
    * @param appName
    * @param taskType
    * @param taskName
    * @param kind
    * @return - Task ID in String
    */
  override def createTask(
    id: String,
    appName: String,
    taskType: String,
    taskName: String,
    kind: TaskKind.Value,
    inputs: JsValue,
    // env: JsObject
  ): Future[String] = {
    // Constructing a body object for the WS post
    def constructPostBody: Future[JsValue] = Future {
      // Validate the inputs
      val validator = new Validation()
      // Zips the task if it's not zipped already
      fs.zipTaskIfNotExist(
        appName, taskType, taskName, true)
      val encodedAction = fs.getActionAsBase64(appName, taskType, taskName)
      JsObject(Seq(
        "exec" -> JsObject(Seq(
          "kind" -> JsString(kind.toString),
          "code" -> JsString(encodedAction)
        ))
      ))
    }
    // Posting a request using the constructed body and retrieves the task name only
    def futureRequest(body: JsValue): Future[String] = {
      // TODO: Abstract api, v1, guest, actions
      ws.url(s"https://${config.WHISK_HOST}/api/v1/namespaces/guest/actions/${id}-${appName}-${taskType}-${taskName}")
      .withHttpHeaders("Accept" -> "application/json")
      .withAuth(config.WHISK_USER, config.WHISK_PASS, WSAuthScheme.BASIC)
      .put(body)
      .map(rawResult => {
        val jsonBody:JsValue = Json.parse(rawResult.body)
        (jsonBody \ "name").as[String]
      })
    }
    constructPostBody.flatMap(body => futureRequest(body))
  }

  /**
    *
    * @param seqId - An unique ID for the sequence
    * @param taskIds - A list of task IDs
    * @return
    */
  override def createSequence(seqId: String, taskIds: List[String]): Future[String] = {
    def constructBody: Future[JsValue] = Future {
      // Adding namespace to each task ID
      val namespacedTaskIds = taskIds.map(id => s"/guest/${id}")
      JsObject(Seq(
        "exec" -> JsObject(Seq(
          "kind" -> JsString("sequence"),
          "components" -> Json.toJson(namespacedTaskIds)
        ))
      ))
    }
    def futureRequest(body: JsValue): Future[String] = {
      // TODO: Abstract api, v1, guest, actions, sequenceAction
      ws.url(s"https://${config.WHISK_HOST}/api/v1/namespaces/guest/actions/${seqId}")
        .withHttpHeaders("Accept" -> "application/json")
        .withAuth(config.WHISK_USER, config.WHISK_PASS, WSAuthScheme.BASIC)
        .put(body)
        .map(rawResult => {
          val jsonBody:JsValue = Json.parse(rawResult.body)
          (jsonBody \ "name").as[String]
        })
    }
    constructBody.flatMap(body => futureRequest(body))
  }

  /**
    * Returns a list of tasks in the current OpenWhisk instance in a string format
    *
    * @example
    * listTasks().flatMap(response => println(response))
    * // list of tasks in String JSON format
    *
    * @return
    */
  override def listTasks(): Future[String] = {
    ws.url(s"https://${config.WHISK_HOST}/api/v1/namespaces/guest/actions")
      .withAuth(config.WHISK_USER, config.WHISK_PASS, WSAuthScheme.BASIC)
      .get()
      .map { response => response.body }
  }
}