package services
import play.api.libs.json._
import scala.collection.mutable.ArrayBuffer
import utils._

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
    * TODO: Remove it or move it to GraphUtil
    * @param edges
    * @param from
    * @param to
    */
  def findEdgeById(edges: List[JsValue], from: String, to: String): Unit = {
    edges.filter((x: JsValue) => (x \ "from").as[String] == from && (x \ "to").as[String] == to).head
  }

  def createSequence(graph: JsValue, sequence: List[String]): Unit = {
    // create tasks
    println("creating a sequence for", sequence)
    sequence.foreach(x => createTask(graph, x))
  }

  def createTask(graph: JsValue, id: String): Unit ={
    val util = new GraphUtil
    print("create task key", id)
    val taskNode = util.getNodesByKeyVal(graph, "id", id)

    println("Found a task node", taskNode)
  }
  /**
    * Creates a pipeline according to the pipelineScript. It should create multidimentional sequences and
    * track the pipeline as a database entity, which can be later retrieved, deleted or updated.
    * @param id
    */
  def create(graph: JsValue): Boolean = {
    val util = new GraphUtil
    val triggerNodes = (graph \ "nodes").as[List[JsValue]].filter(x => (x \ "taskType").as[String] == "trigger")
    val paths = triggerNodes
      .map(x => util.getAllPaths(graph, (x \ "id").as[String]))
      .flatten
    // Loop each flattened paths
    paths.foreach(x => createSequence(graph, x))
    true
  }
}
