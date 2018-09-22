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

import dao.CatDAO
import dao.DogDAO
import models.Cat
import models.Dog

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
  catDao: CatDAO,
  dogDao: DogDAO,
  cc: MessagesControllerComponents,
)(implicit assetsFinder: AssetsFinder) extends MessagesAbstractController(cc) {

  val catForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> text(),
      "color" -> text()
    )(Cat.apply)(Cat.unapply _)
  )

  val dogForm = Form(
    mapping(
      "name" -> text(),
      "color" -> text()
    )(Dog.apply)(Dog.unapply _)
  )

  def index = Action.async { implicit request =>
    catDao.all().zip(dogDao.all()).map {
      case (cats, dogs) => Ok(views.html.newindex(catForm, cats, dogs))
    }
  }

  def insertCat = Action.async { implicit request =>
    val cat: Cat = catForm.bindFromRequest.get
    catDao.insert(cat).map(_ => Redirect(routes.HomeController.index))
  }

  def deleteCat(id: Option[Long]) = Action.async { implicit request =>
    catDao.delete(id).map(_ => Redirect(routes.HomeController.index))
  }

  def insertDog = Action.async { implicit request =>
    val dog: Dog = dogForm.bindFromRequest.get
    dogDao.insert(dog).map(_ => Redirect(routes.HomeController.index))
  }
}
