package models

object DistributedProtocol {


  case class WorkerRegister(workerId: String)
  case class WorkerRequestTask(workerId: String)

  case class TaskCompleted(workerId: String, taskId: String, result: Any)
  case class TaskFailed(workerId: String, taskId: String)

  case object TaskAvailable

}
