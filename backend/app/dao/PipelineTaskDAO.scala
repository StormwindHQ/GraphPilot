package dao

import scala.concurrent.{ Future }

import models.PipelineTask

class PipelineTaskDAO {
  private val data = Seq(
    new PipelineTask(
      id=Some(1),
      attributes="test",
      nextTask=Some(2),
      pipeline=Some(1)
    ),
    new PipelineTask(
      id=Some(2),
      attributes="hey",
      nextTask=null,
      pipeline=Some(1)
    ),
  )

  def all(): Seq[PipelineTask] = data

  def findById(id: Option[Long]): PipelineTask = data.filter(_.id == id).head

  def findByPipeline(id: Option[Long]): Seq[PipelineTask] = data.filter(_.pipeline == id)
}
