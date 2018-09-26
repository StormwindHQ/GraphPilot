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
import services.WskService

import play.api.libs.ws._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class OpenWhiskExp @Inject()(
  cc: MessagesControllerComponents,
  wsk: WskService,
  ws: WSClient
)(implicit assetsFinder: AssetsFinder) extends MessagesAbstractController(cc) {
  def index = Action.async {
    wsk.getNamespaces().map { response => Ok(response.body) }
  }

}
