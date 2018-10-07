package services
import play.api.libs.json._

/**
  MVP 1 Plan

  1. Add a single fake data store, which is similar to the Counter example
  2. Add initial fake in-memory pipelines
  3. Implement the pipeline process
  */

class PipelineService {
  /**
    * Creates a pipeline according to the pipelineScript. It should create multidimentional sequences and
    * track the pipeline as a database entity, which can be later retrieved, deleted or updated.
    * @param id
    */
  def create(pipelineScript: JsValue): Boolean = {
    val nodes = (pipelineScript \ "nodes").as[List[JsValue]]
    val edges = (pipelineScript \ "edges").as[List[JsValue]]
    print("nodes", nodes, edges)
    true
  }
}
