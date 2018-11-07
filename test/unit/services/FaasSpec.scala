import javax.inject._
import scala.concurrent.ExecutionContext
import org.scalatestplus.play._
import services.WskService


/**
  * Unit tests can run without a full Play application.
  */
class FaasSpec extends PlaySpec {
  implicit val executionContext = ExecutionContext
  "WskService" should {
    "listNamespaces" should {
      "return guest namespace" in {
        val faas = new WskService
        val namespaces = faas.listNamespaces
        println("checking namespaces", namespaces)
        namespaces must equal("test")
      }
    }
  }
}