package models

case class Cat(id: Option[Long] = None, name: String, color: String)

case class Dog(name: String, color: String)

/**
  * Pod represents an app.
  * Example of pod include SlackPod, DiscordPod and etc
  */
case class Pod()

/**
  * PodTask is an individual task associated to a pod
  * Task type can be either trigger, action or condition
  * @param id
  */
case class PodTask()

/**
  * Pipeline is a representation of an automated task created by the user
  * It has relationships with multiple PipelineTasks and must have at least 1
  * trigger PipelineTask
  */
case class Pipeline(
  id: Option[Long]
)

/**
  * Task that is currently running and associated to a pipeline
  */
case class PipelineTask(
  id: Option[Long],
  attributes: String,
  // Next task in the chain
  nextTask: Option[Long],
  pipeline: Option[Long]
)
