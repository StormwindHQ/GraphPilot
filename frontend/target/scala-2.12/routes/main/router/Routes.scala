// @GENERATOR:play-routes-compiler
// @SOURCE:/home/jason/Desktop/Stormwind-new/backend/conf/routes
// @DATE:Mon Sep 17 19:53:45 AEST 2018

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:6
  HomeController_0: controllers.HomeController,
  // @LINE:15
  CountController_3: controllers.CountController,
  // @LINE:17
  AsyncController_2: controllers.AsyncController,
  // @LINE:20
  Assets_1: controllers.Assets,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:6
    HomeController_0: controllers.HomeController,
    // @LINE:15
    CountController_3: controllers.CountController,
    // @LINE:17
    AsyncController_2: controllers.AsyncController,
    // @LINE:20
    Assets_1: controllers.Assets
  ) = this(errorHandler, HomeController_0, CountController_3, AsyncController_2, Assets_1, "/")

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, HomeController_0, CountController_3, AsyncController_2, Assets_1, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.HomeController.index"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """insert/cat""", """controllers.HomeController.insertCat"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """insert/dog""", """controllers.HomeController.insertDog"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """delete/cat/""", """controllers.HomeController.deleteCat(id:Option[Long])"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """count""", """controllers.CountController.count"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """message""", """controllers.AsyncController.message"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/""" + "$" + """file<.+>""", """controllers.Assets.versioned(file:String)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:6
  private[this] lazy val controllers_HomeController_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_HomeController_index0_invoker = createInvoker(
    HomeController_0.index,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HomeController",
      "index",
      Nil,
      "GET",
      this.prefix + """""",
      """ An example controller showing a sample home page""",
      Seq()
    )
  )

  // @LINE:9
  private[this] lazy val controllers_HomeController_insertCat1_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("insert/cat")))
  )
  private[this] lazy val controllers_HomeController_insertCat1_invoker = createInvoker(
    HomeController_0.insertCat,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HomeController",
      "insertCat",
      Nil,
      "POST",
      this.prefix + """insert/cat""",
      """ Cat Dog inserts""",
      Seq()
    )
  )

  // @LINE:10
  private[this] lazy val controllers_HomeController_insertDog2_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("insert/dog")))
  )
  private[this] lazy val controllers_HomeController_insertDog2_invoker = createInvoker(
    HomeController_0.insertDog,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HomeController",
      "insertDog",
      Nil,
      "POST",
      this.prefix + """insert/dog""",
      """""",
      Seq()
    )
  )

  // @LINE:12
  private[this] lazy val controllers_HomeController_deleteCat3_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("delete/cat/")))
  )
  private[this] lazy val controllers_HomeController_deleteCat3_invoker = createInvoker(
    HomeController_0.deleteCat(fakeValue[Option[Long]]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HomeController",
      "deleteCat",
      Seq(classOf[Option[Long]]),
      "POST",
      this.prefix + """delete/cat/""",
      """""",
      Seq()
    )
  )

  // @LINE:15
  private[this] lazy val controllers_CountController_count4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("count")))
  )
  private[this] lazy val controllers_CountController_count4_invoker = createInvoker(
    CountController_3.count,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CountController",
      "count",
      Nil,
      "GET",
      this.prefix + """count""",
      """ An example controller showing how to use dependency injection""",
      Seq()
    )
  )

  // @LINE:17
  private[this] lazy val controllers_AsyncController_message5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("message")))
  )
  private[this] lazy val controllers_AsyncController_message5_invoker = createInvoker(
    AsyncController_2.message,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AsyncController",
      "message",
      Nil,
      "GET",
      this.prefix + """message""",
      """ An example controller showing how to write asynchronous code""",
      Seq()
    )
  )

  // @LINE:20
  private[this] lazy val controllers_Assets_versioned6_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned6_invoker = createInvoker(
    Assets_1.versioned(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String]),
      "GET",
      this.prefix + """assets/""" + "$" + """file<.+>""",
      """ Map static resources from the /public folder to the /assets URL path""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:6
    case controllers_HomeController_index0_route(params@_) =>
      call { 
        controllers_HomeController_index0_invoker.call(HomeController_0.index)
      }
  
    // @LINE:9
    case controllers_HomeController_insertCat1_route(params@_) =>
      call { 
        controllers_HomeController_insertCat1_invoker.call(HomeController_0.insertCat)
      }
  
    // @LINE:10
    case controllers_HomeController_insertDog2_route(params@_) =>
      call { 
        controllers_HomeController_insertDog2_invoker.call(HomeController_0.insertDog)
      }
  
    // @LINE:12
    case controllers_HomeController_deleteCat3_route(params@_) =>
      call(params.fromQuery[Option[Long]]("id", None)) { (id) =>
        controllers_HomeController_deleteCat3_invoker.call(HomeController_0.deleteCat(id))
      }
  
    // @LINE:15
    case controllers_CountController_count4_route(params@_) =>
      call { 
        controllers_CountController_count4_invoker.call(CountController_3.count)
      }
  
    // @LINE:17
    case controllers_AsyncController_message5_route(params@_) =>
      call { 
        controllers_AsyncController_message5_invoker.call(AsyncController_2.message)
      }
  
    // @LINE:20
    case controllers_Assets_versioned6_route(params@_) =>
      call(params.fromPath[String]("file", None)) { (file) =>
        controllers_Assets_versioned6_invoker.call(Assets_1.versioned(file))
      }
  }
}
