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
  val graph1: JsValue = Json.parse("""
  {
   "nodes": [
     { "id": "task_1", "guid": "trigger_12938x12938", "taskApp": "github", "taskType": "trigger", "taskName": "on_wiki_update", "chart": { "x": 12, "y": 39 } },
     { "id": "task_2", "guid": "action_12983xcv", "taskApp": "github", "taskType": "action", "taskName": "create_issue", "chart": { "x": 55, "y": 203 } },
     { "id": "task_3", "guid": "action_3432aa", "taskApp": "conditions", "taskType": "condition", "taskName": "wait", "chart": { "x": 232, "y": 111 } },
     { "id": "task_4", "guid": "action_634643asd1", "taskApp": "github", "taskType": "action", "taskName": "create_commit", "chart": { "x": 312, "y": 11 } },
     { "id": "task_5", "guid": "trigger_3928429xx", "taskApp": "discord", "taskType": "trigger", "taskName": "on_new_message", "chart": { "x": 91, "y": 211 } } ],
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
     },
     {
       "from": "task_5",
       "to": "task_4",
       "payload": {
         "message": "Making a new commit out of a new message \"${task_5.message}\" from discord!"
       }
     }
   ]
  }
""")
  "GraphUtil:getDirectSuccessors" should {
    "return a correct single successor for task_1" in {
      val graphUtil = new GraphUtil
      val result = graphUtil.getDirectSuccessors(graph1, "task_1")
      val expected = List("task_2")
      result must equal(expected)
    }
    "return a multiple successors for task_2" in {
      val graphUtil = new GraphUtil
      val result = graphUtil.getDirectSuccessors(graph1, "task_2")
      val expected = List("task_3", "task_4")
      result must equal(expected)
    }
    // Test graph loops
  }

  "GraphUtil:getAllPaths" should {
    "return multiple paths from the starting point task_1" in {
      val graphUtil = new GraphUtil
      val result = graphUtil.getAllPaths(graph1, "task_1")
      val expected = List(List("task_1", "task_2", "task_3", "task_4"), List("task_1", "task_2", "task_4"))
      result must equal(expected)
    }
  }
}