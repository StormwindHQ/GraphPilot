import akka.actor.ActorSystem
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import org.mockito.Mockito._
import play.api.libs.json._
import utils.GraphUtil

class GraphUtilSpec extends PlaySpec {

  "GraphUtil:getAllPaths" should {
    "return multiple paths from the starting point task_1" in {
      val graphUtil = new GraphUtil
      val result = graphUtil.getAllPaths(graph1, "task_1")
      val expected = List(List("task_1", "task_2", "task_3", "task_4"), List("task_1", "task_2", "task_4"))
      result must equal(expected)
    }
    "result a single path from the starting point task_5" in {
      val graphUtil = new GraphUtil
      val result = graphUtil.getAllPaths(graph1, "task_5")
      val expected = List(List("task_5", "task_4"))
      result must equal(expected)
    }
  }
}