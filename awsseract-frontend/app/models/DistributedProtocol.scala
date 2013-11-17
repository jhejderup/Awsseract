package models

import main.scala.nl.in4392.models.WorkerStatusProtocol.WorkerState

import akka.actor.{ ActorRef, Props, Actor, ActorSystem }

object DistributedProtocol {

  //Worker
  case class WorkerRegister(workerId: String)
  case class WorkerRequestTask(workerId: String)
  case class WorkerDeregister(workerId: String)

  case class TaskCompleted(workerId: String, taskId: String, result: Any, taskInfo: String)
  case class TaskFailed(workerId: String, taskId: String, taskInfo: String)

  case object TaskAvailable

  //Monitoring
  case object RequestSystemInfo
  case class ReportSystemInfo(workerId:String,jsonString:String)
  case class MonitorRegister(workerId: String)

  //Instance Managing
  case class SystemStatus(jobSize: Int, workers: Map[String,WorkerState])
  case object ManageInstance
  case object StartInstanceManager
  case object RequestSystemStatus
}
