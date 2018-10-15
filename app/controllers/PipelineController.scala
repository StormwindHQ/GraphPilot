package controllers

import javax.inject._

import play.api.db._
import play.api.mvc._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._

import play.api.data.Form
import play.api.data.Forms.{ date, longNumber, mapping, nonEmptyText, optional, text }
import play.filters.csrf._
import play.filters.csrf.CSRF.Token
import play.api.libs.json._
import services.{ WskService, TaskKind, PipelineService }
import utils.{ GraphUtil }
import consts.{ MultipleTaskNodeException }

import models.Pipeline

import play.api.libs.ws._

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
  ps: PipelineService,
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
  def createPipeline = Action { implicit request =>

    val graph: JsValue = Json.parse("""
      {
       "nodes": [
         { "id": "task_1", "guid": "trigger_12938x12938", "taskApp": "github", "taskType": "trigger", "taskName": "on_wiki_update", "chart": { "x": 12, "y": 39 } },
         { "id": "task_2", "guid": "action_12983xcv", "taskApp": "github", "taskType": "action", "taskName": "create_issue", "chart": { "x": 55, "y": 203 } },
         { "id": "task_3", "guid": "action_3432aa", "taskApp": "conditions", "taskType": "condition", "taskName": "wait", "chart": { "x": 232, "y": 111 } },
         { "id": "task_4", "guid": "action_634643asd1", "taskApp": "github", "taskType": "action", "taskName": "render_markdown", "chart": { "x": 312, "y": 11 } } ],
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

    val triggerNodes = (graph \ "nodes").as[List[JsValue]].filter(x => (x \ "taskType").as[String] == "trigger")
    val paths = triggerNodes
      .map(x => graphUtil.getAllPaths(graph, (x \ "id").as[String]))
      .flatten

    paths.foreach(sequence => createSequence(graph, sequence))

    Ok("testing!")
  }

  // Useful methods

  /**
    * Creating sequence factory method
    * @param graph
    * @param sequence
    */
  def createSequence(graph: JsValue, sequence: List[String]): Unit = {
    // create tasks
    sequence.foreach(task => createTask(graph, task))
  }

  /**
    * Create task factory method
    * @param graph
    * @param id
    */
  def createTask(graph: JsValue, id: String): Unit ={
    val util = new GraphUtil
    print("create task key", id)

    // It should only return one task node
    val rawTaskSearch = graphUtil.getNodesByKeyVal(graph, "id", id)
    if (rawTaskSearch.length > 1) {
      throw new MultipleTaskNodeException
    }
    val task = rawTaskSearch.head
    faas.createTask(
      appName=(task \ "taskApp").as[String],
      taskType=(task \ "taskType").as[String],
      taskName=(task \ "taskName").as[String],
      kind=TaskKind.node8,
      inputs=null
    )
  }

}
