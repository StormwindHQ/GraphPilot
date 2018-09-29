import akka.actor.ActorSystem
import org.scalatestplus.play._
import javax.inject.Inject
import play.core.server.Server
import play.api.routing.sird._
import play.api.mvc._
import play.api.libs.json._
import play.api.test._

import scala.concurrent.Await
import scala.concurrent.duration._

import org.scalatestplus.play._

import services.WskService

class WskServiceSpec @Inject()(
  ws: WskService
) extends PlaySpec {
  import scala.concurrent.ExecutionContext.Implicits.global

  "WskService" should {
    "get all repositories" in {

      Server.withRouterFromComponents() { components =>
        import Results._
        import components.{ defaultActionBuilder => Action }
      {
        case GET(p"/api/v1/namespaces") => Action {
          Ok(Json.arr("guest"))
        }
      }
      } { implicit port =>
        WsTestClient.withClient { client =>
          val result = Await.result(ws.listNamespaces(), 10.seconds)
          result must equal("gueszxcxzt")
        }
      }
    }
  }
}
