package main.scala.nl.in4392.worker

import nl.in4392.models.DistributedProtocol._
import nl.in4392.models.Task._

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.actor.ActorLogging
import akka.actor.ActorPath
import java.util.UUID

class WorkerActor(masterPath: ActorPath) extends Actor with ActorLogging {

  val master = context.actorSelection(masterPath)
  val workerId = UUID.randomUUID().toString

  val system = ActorSystem("JobExecutorSystem")
  // default Actor constructor
  val jobExecutor = system.actorOf(Props[JobExecutorActor], "jobexec")


  override def preStart() = {
    println("Inside preStart")
       master ! "hello"
       master ! WorkerRegister(workerId)

  }

  def stateIdle: Receive = {

    case TaskAvailable  =>
      master ! WorkerRequestTask(workerId)
    case task: Task =>
      println("got a job!! ")
      println("Job info: id {}, job {}",task.taskId)
      jobExecutor ! task
      context.become(stateWorking())
  }

  def stateWorking(): Receive = {

    case TaskResult(taskId,result) =>
      println("Task Finished. Result {}.", result)
      master ! TaskCompleted(workerId,taskId,result)
      master ! WorkerRequestTask(workerId)
      context.become(stateIdle)

    case _: Task =>
      log.info("Already working!")
  }



  def receive = stateIdle

}
