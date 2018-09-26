package services
import javax.inject._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import scala.concurrent.Future
import play.api.libs.ws._
import play.api.http.HttpEntity

@Singleton
class WskService @Inject() (ws: WSClient)(implicit executionContext: ExecutionContext) {
  def getNamespaces(wskType: String = "web"): Future[WSResponse] = {
    if (wskType == "web") {
      // TODO: String interpolation + abstracted value
      ws.url("https://localhost/api/v1/namespaces")
        .withAuth("23bc46b1-71f6-4ed5-8c54-816aa4f8c502", "123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP", WSAuthScheme.BASIC)
        .get()
        .map { response => response }
    } else {
      ws.url("http://somesite.com").get().map { response => response }
    }
  }
}
