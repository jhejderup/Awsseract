package main.scala.nl.in4392.worker

import main.scala.nl.in4392.models.DistributedProtocol._
import main.scala.nl.in4392.models.Task._

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.actor.ActorLogging
import akka.actor.ActorPath
import java.util.UUID
import nl.tudelft.ec2interface.taskmonitor.TaskInfo
import java.sql.Timestamp
import akka.util.Timeout
import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.duration._
import akka.pattern.{ ask, pipe }
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{ Success, Failure }
import nl.tudelft.ec2interface.instancemanager._

class WorkerActor(workerId: String,masterPath: ActorPath) extends Actor with ActorLogging {

  val instanceId = new RemoteActorInfo().getInfoFromFile("conf/masterInfo").getSelfInstanceID

  val master = context.actorSelection(masterPath)
  val system = ActorSystem("JobExecutorSystem")
  val jobExecutor = system.actorOf(Props[JobExecutorActor], "jobexec")


  override def preStart() = {
    log.info("[WorkerActor] Register worker {} with master", workerid)
    master ! WorkerRegister(workerId)

  }

  override def postRestart(err:Throwable) = {
    log.info("[WorkerActor] Restarted and continues from the state before the exception")
  }
  def receive = {
    case TaskAvailable  =>
      master ! WorkerRequestTask(workerId)
    case task: Task =>
      var tInfo = new TaskInfo().FromJson(task.taskInfo)
      tInfo.setWorkerId(instanceId)
      tInfo.setStartTime(new Timestamp(System.currentTimeMillis()))
      log.info("[WorkerActor] Received Task: id {}, job {}",task.taskId, task.taskInfo.toString)
      implicit val timeout = Timeout(10 seconds)
      val resultFuture = jobExecutor ? new Task(task.taskId,task.job,new TaskInfo().ToJson(tInfo))
      resultFuture onComplete {
        case Success(TaskResult(taskId,result,taskInfo)) =>
          log.info("[WorkerActor] Task Finished. Result {}.", result)
          var tInfo = new TaskInfo().FromJson(taskInfo)
          tInfo.setFinishTime(new Timestamp(System.currentTimeMillis()))
          master ! TaskCompleted(workerId,taskId,result,new TaskInfo().ToJson(tInfo))
          master ! WorkerRequestTask(workerId)
        case Failure(t) =>
          t.printStackTrace()
      }
  }
}
