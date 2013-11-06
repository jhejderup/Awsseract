package models

object Task {

  case class Task(taskId: String, job: Any)
  case class TaskResult(taskId: String, result: Any)

}
