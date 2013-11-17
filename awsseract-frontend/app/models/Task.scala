package models

import nl.tudelft.ec2interface.taskmonitor._
object Task {

  case class Task(taskId: String, job: Any, taskInfo: String)
  case class TaskResult(taskId: String, result: Any, taskInfo: String)

}
