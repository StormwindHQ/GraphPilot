package controllers

import javax.inject._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.db._
import play.api.mvc._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import play.api.data.Form
import play.api.data.Forms.{date, longNumber, mapping, nonEmptyText, optional, text}
import play.filters.csrf._
import play.filters.csrf.CSRF.Token
import play.api.libs.json._
import models.Pipeline
import play.api.libs.ws._
import consts.{ MultipleTaskNodeException }
import services.{ WskService, TaskKind }
import utils.{ GraphUtil }
import scala.collection.mutable.ListBuffer

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class PipelineController @Inject()(
  cc: MessagesControllerComponents,
  faas: WskService, // TODO: How do we inject dependency according to the settings? e.g. faas=aws should inject AwsService
  ws: WSClient,
  graphUtil: GraphUtil,
)(implicit assetsFinder: AssetsFinder) extends MessagesAbstractController(cc) {

  val pipelineForm = Form(
    mapping(
      "id"  -> optional(longNumber)
    )(Pipeline.apply)(Pipeline.unapply)
  )

  def index = Action.async { implicit request =>
    faas.listNamespaces().map {
      response => Ok(views.html.index(response, pipelineForm))
    }
  }

  /**
    * Add a pipeline
    * @return
    */
  def createPipeline = Action.async { implicit request =>

    val graph: JsValue = Json.parse("""
      {
       "nodes": [
         { "id": "task_1", "guid": "trigger_12938x12938", "taskApp": "github", "taskType": "triggers", "taskName": "on_wiki_update", "chart": { "x": 12, "y": 39 } },
         { "id": "task_2", "guid": "action_12983xcv", "taskApp": "github", "taskType": "actions", "taskName": "create_issue", "chart": { "x": 55, "y": 203 } },
         { "id": "task_3", "guid": "action_3432aa", "taskApp": "conditions", "taskType": "actions", "taskName": "wait", "chart": { "x": 232, "y": 111 } },
         { "id": "task_4", "guid": "action_634643asd1", "taskApp": "github", "taskType": "actions", "taskName": "render_markdown", "chart": { "x": 312, "y": 11 } } ],
       "edges": [
         {
           "from": "task_1",
           "to": "task_2",
           "payload": {
             "title": "Creating a new issue for fun!",
             "body": "${task_1.createdDate} ${task_1.title} was updated just now!"
           }
         },
         {
           "from": "task_2",
           "to": "task_3",
           "payload": {
             "title": "Creating a new issue for fun!",
             "body": "${task_1.createdDate}",
             "delay": 5000
           }
         },
         {
           "from": "task_2",
           "to": "task_4"
         },
         {
           "from": "task_3",
           "to": "task_4",
           "payload": {
             "delay": 5000,
             "message": "As a result of wiki article ${task_1.title} update, now the system will make a new commit"
           }
         }
       ]
      }
    """)
    val triggerNodes = (graph \ "nodes").as[List[JsValue]].filter(x => (x \ "taskType").as[String] == "triggers")
    // Get all the paths according to the trigger nodes
    val paths = triggerNodes
      .map(x => graphUtil.getAllPaths(graph, (x \ "id").as[String]))
      .flatten
    // val deepFlatPaths = paths.flatten
    // 1. filter only action tasks
    // 2. create future maps

    val sequences = paths.map(sequence => createSequence(graph, sequence))
    println("sequences", sequences)
    for (eachFuture <- sequences) {
      val seqResult = Await.result(eachFuture, Duration.Inf)
      println("checking future", seqResult)
    }
    val sequenceFutures = Future.sequence(sequences)
    sequenceFutures.map(result =>
      Ok("test" + result mkString "/"))
  }

  // Useful methods

  /**
    * Creates a list of Future sequences that looks like
    * List(Future(<not completed>), Future(<not completed>)
    * @param graph
    * @param sequence
    */
  def createSequence(graph: JsValue, sequence: List[String]): Future[List[String]] = Future {
    /*def futureCreateTasks: Future[List[String]] = Future {
      val futureInputs = sequence
        .filter((taskId: String) => (graphUtil.getNodesByKeyVal(graph, "id", taskId).head \ "taskType").as[String] == "actions")
        .map((taskId: String) => (graph, taskId))
      // pipelineCreateTask
      // Future.traverse(futureInputs)(args => pipelineCreateTask(args._1, args._2)).map(x => x)
    }*/
    /*val futureInputs = sequence
      .filter((taskId: String) => (graphUtil.getNodesByKeyVal(graph, "id", taskId).head \ "taskType").as[String] == "actions")
      .map((taskId: String) => (graph, taskId))
    println("future inputs ", futureInputs)*/
    val tasks = sequence
      .filter((taskId: String) => (graphUtil.getNodesByKeyVal(graph, "id", taskId).head \ "taskType").as[String] == "actions")
    var taskIds = new ListBuffer[String]()
    for (taskId <- tasks) {
      val createTaskResult = Await.result(pipelineCreateTask(graph, taskId), Duration.Inf)
      createTaskResult match {
        case x: String => taskIds = taskIds += x
      }
    }
    taskIds.toList
  }

  /**
    * Creating a future to create an OpenWhisk task. It expects a static graph and an ID of a task
    * to find the detail about the node from the graph.
    * @param graph
    * @param id
    */
  def pipelineCreateTask(graph: JsValue, id: String): Future[String] = {

    // Find the first task according to the taskId and the static graph
    def findTask: Future[JsValue] = Future {
      val util = new GraphUtil
      val rawTaskSearch = graphUtil.getNodesByKeyVal(graph, "id", id)
      if (rawTaskSearch.length > 1) {
        throw new MultipleTaskNodeException
      }
      rawTaskSearch.head
    }

    // Create the task accordingly
    def createTask(task: JsValue): Future[String] = {
      faas.createTask(
        appName=(task \ "taskApp").as[String],
        taskType=(task \ "taskType").as[String],
        taskName=(task \ "taskName").as[String],
        kind=TaskKind.node8,
        inputs=null
      )
    }

    findTask.flatMap(task => createTask(task))
  }

}
