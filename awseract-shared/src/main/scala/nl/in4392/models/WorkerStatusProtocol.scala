package main.scala.nl.in4392.models

import akka.actor.ActorRef

object WorkerStatusProtocol {
  import Task._
  trait WorkerStatus
  case class Working(task: Task) extends WorkerStatus
  case object Idle extends WorkerStatus


  case class WorkerState(ref: ActorRef, status: WorkerStatus)


}
