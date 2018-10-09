package services
import play.api.libs.json._
import scala.collection.mutable.ArrayBuffer

/**
  MVP 1 Plan

  1. Add a single fake data store, which is similar to the Counter example
  2. Add initial fake in-memory pipelines
  3. Implement the pipeline process
  */

class PipelineService {

  /**
    * Finds an edge in a edges list that matches both from and to
    *
    * @example
    * val inputs: JsValue = Json.parse("""
    * {
    *   "edges": [ { "from": "task_1", "to": "task_2" }, { "from": "task_2", "to": "task_3" } ]
    * }
    * """)
    *
    * findEdgeById((inputs \ "edges").as[List[JsValue]], "task_1", "task_2)
    * // returns { "from": "task_1", "to": "task_2" }
    *
    * @param edges
    * @param from
    * @param to
    */
  def findEdgeById(edges: List[JsValue], from: String, to: String): Unit = {
    edges.filter((x: JsValue) => (x \ "from").as[String] == from && (x \ "to").as[String] == to).head
  }
  /**
    * Creates a pipeline according to the pipelineScript. It should create multidimentional sequences and
    * track the pipeline as a database entity, which can be later retrieved, deleted or updated.
    * @param id
    */
  def create(pipelineScript: JsValue): Boolean = {
    val nodes = (pipelineScript \ "nodes").as[List[JsValue]]
    val edges = (pipelineScript \ "edges").as[List[JsValue]]
    // From nodes, all the trigger type nodes
    val triggers = nodes.filter((x: JsValue) => (x \ "taskType").as[String] == "trigger")
    // From edges, find everything with trigger as "from"
    val edgesWithTrigger = edges.filter( (x: JsValue) =>
      triggers.find( (y: JsValue) => (y \ "id").as[String] == (x \ "from").as[String] ).isDefined )

    // Loop each trigger in the nodes list
    // example output
    // [ [1], [2, 3, 4] [2, 4], [5, 4] ]
    for (targetEdge <- edgesWithTrigger) {
      // Target edge is the starting node for the traversing the tree

      val list = ArrayBuffer[Any]()
      def traverse(tree: List[JsValue], currentEdge: JsValue): Unit = {
        val from = (currentEdge \ "from").as[String]
        val to = (currentEdge \ "to").as[String]
        val children = tree.filter((x: JsValue) => (x \ "from").as[String] == to)
        list.append((from, to))
        for (child <- children) {
          // If children is larger than 1 in length, create new arrays
          traverse(edges, child)
        }
      }
      traverse(edges, targetEdge)
    }
    true
  }
}
