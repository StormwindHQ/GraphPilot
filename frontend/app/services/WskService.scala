package services
import javax.inject._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import scala.concurrent.Future
import play.api.libs.ws._
import play.api.http.HttpEntity

/**
  * WskService.scala
  *
  * Context:
  * The class provides interface to OpenWhisk backend. Username and password are
  * mandatory security requirements and must be provided in order to operate this class.
  * Naming convention for each utility method follows:
  * [R] get...
  * [W] create..
  * [U] update..
  * [D] delete..
  *
  * @param ws
  * @param fileEncoder
  * @param executionContext
  */
@Singleton
class WskService @Inject() (
  ws: WSClient,
  fileEncoder: FileEncoder
)(implicit executionContext: ExecutionContext) {
  /**
    * Get available name spaces in the OpenWhisk instance
    * @param wskType
    * @return Future<String>
    */
  def getNamespaces(wskType: String = "web"): Future[String] = {
    // TODO: String interpolation + abstracted value
    fileEncoder.getActionAsBase64("github", "actions", "create_issue")
    ws.url("https://localhost/api/v1/namespaces")
      .withAuth("23bc46b1-71f6-4ed5-8c54-816aa4f8c502", "123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP", WSAuthScheme.BASIC)
      .get()
      .map { response => response.body }
  }

  def createAction(base64: String = null) = {
    fileEncoder
  }
}
