package utils

import play.api.libs.json._

/**
  * A custom graph theory utility based on the simplicity
  *
  * Philosophies
  * - Given a JSON, return a pure value
  * - Given an value and a JSON, result a JSON
  * - Given a JSON, return a JSON
  * - Strictly follows the Graph Theory
  * - JSON data is based on ScalaJson
  */
class GraphUtil {

  /**
    * Retreives a simple list of direct successors' IDs from a node
    *
    * @example
    * // .. [{ from: "t1", to: "t2" }, { from: "t2", to: "t3" }, { from: "t3", to: "t4" }]
    * getDirectSuccessors(graph, "t2")
    * // Result: [ "t3" ]
    *
    * @param json
    * @param node
    * @return
    */
  def getDirectSuccessors(graph: JsValue, node: String): List[String] = {
    val edges = (graph \ "edges").get
    val sources = edges.as[List[JsValue]].filter(x => (x \ "from").as[String] == node)
    sources.map(x => (x \ "to").as[String])
  }

  /**
    * Get all paths until there are no more direct successors left
    *
    * @example
    * // .. [{ from: "t1", to: "t2" }, { from: "t2", to: "t3" }, { from: "t3", to: "t4" }]
    * getAllPaths(graph, "t1")
    * // Result: [ ["t1", "t2", "t3", "t4"] ]
    *
    * @param graph
    * @param startingNode
    * @return
    */
  def getAllPaths(graph: JsValue, startingNode: String): List[Any] = {
    def traverse(node: String, paths: List[String]): List[Any] = {
      val directs = getDirectSuccessors(graph, node)
      if (directs.length == 0) {
        // Dead end
        paths
      } else if (directs.length == 1) {
        // Node with single direction, simply returns itself
        if (paths.length == 0) {
          // instead of appending successor, append itself
          traverse(directs(0), paths :+ startingNode :+ directs(0))
        } else {
          traverse(directs(0), paths :+ directs(0))
        }
      } else {
        directs.map(d => {
          traverse(d, paths :+ d)
        })
      }
    }
    val accum = List[String]()
    traverse(startingNode, accum)
  }
}

