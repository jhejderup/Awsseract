package main.scala.nl.in4392.worker

import nl.in4392.models.DistributedProtocol._
import nl.in4392.models.Task._

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.actor.ActorLogging
import akka.actor.ActorPath
import java.util.UUID
import akka.util.Timeout
import scala.concurrent.{Await, Future, ExecutionContext}
import scala.concurrent.duration._
import akka.pattern.{ ask, pipe }
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits._
import scala.util.{ Success, Failure }
class WorkerActor(masterPath: ActorPath) extends Actor with ActorLogging {

  val master = context.actorSelection(masterPath)
  val workerId = UUID.randomUUID().toString

  val system = ActorSystem("JobExecutorSystem")
  val jobExecutor = system.actorOf(Props[JobExecutorActor], "jobexec")


  override def preStart() = {
       println("Inside preStart")
       master ! WorkerRegister(workerId)

  }

  def receive = {
    case TaskAvailable  =>
      master ! WorkerRequestTask(workerId)
    case task: Task =>
      println("got a job!! ")
      println("Job info: id {}, job {}",task.taskId)
      implicit val timeout = Timeout(10 seconds)
      val resultFuture = jobExecutor ? task
      resultFuture onComplete {
        case Success(TaskResult(taskId,result)) =>
          master ! TaskCompleted(workerId,taskId,result)
          master ! WorkerRequestTask(workerId)  
        case Failure(t) =>
          t.printStackTrace()
      }
  }
}
