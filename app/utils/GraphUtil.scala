package utils

import play.api.libs.json._

class GraphUtil {
  def test(): Unit ={
    val inputs: JsValue = Json.parse("""
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
    println(getAllPaths(inputs, "task_1"))
  }

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

