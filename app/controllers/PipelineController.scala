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
import services.{ WskService, TaskKind }

import models.Pipeline

import play.api.libs.ws._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class PipelineController @Inject()(
  cc: MessagesControllerComponents,
  wsk: WskService,
  ws: WSClient
)(implicit assetsFinder: AssetsFinder) extends MessagesAbstractController(cc) {

  val pipelineForm = Form(
    mapping(
      "id"  -> optional(longNumber)
    )(Pipeline.apply)(Pipeline.unapply)
  )

  def index = Action.async { implicit request =>
    wsk.listNamespaces().map {
      response => Ok(views.html.index(response, pipelineForm))
    }
  }

  /**
    * Add a pipeline
    * @return
    */
  def createPipeline = Action.async { implicit request =>

    val inputs: JsValue = JsObject(Seq(
      "title" -> JsString("Hello world!"),
      "body" -> JsString("Hey!!"),
      "state" -> JsString("open"),
      "labels" -> JsArray(IndexedSeq(
        JsString("bug")
      )),
      "assignees" -> JsArray(IndexedSeq(
        JsString("Jason")
      ))
    ))
    wsk.createTask(
      appName="github",
      taskType="actions",
      taskName="create_issue",
      kind=TaskKind.node6,
      inputs=inputs
    ).map { response => Ok(response) }
  }

}
