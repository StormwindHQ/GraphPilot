package dao

import scala.concurrent.{ Future }

import models.Pipeline

class PipelineDAO {
  private val data = Seq(
    new Pipeline(Some(1)),
    new Pipeline(Some(2)),
  )

  def all(): Seq[Pipeline] = data

  def findById(id: Option[Long]): Pipeline = data.filter(_.id == id).head
}
