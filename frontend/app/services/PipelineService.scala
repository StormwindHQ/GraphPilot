package services

import dao.{ PipelineDAO, PipelineTaskDAO }

/**
  MVP 1 Plan

  1. Add a single fake data store, which is similar to the Counter example
  2. Add initial fake in-memory pipelines
  3. Implement the pipeline process
  */

class PipelineService {
  private val pipelineTaskDao: PipelineTaskDAO = new PipelineTaskDAO
  /**
    * Run an entire pipeline in memory
    * @param id
    */
  def runInMemory(id: Option[Long]): Unit = {
    print("Run a pipeline service")
    val pipelineTasks = pipelineTaskDao.findByPipeline(id)
    pipelineTasks.foreach {
      println
    }
  }
}
