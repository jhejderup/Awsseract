package nl.in4392.master


import akka.actor.Actor
import akka.actor.ActorLogging
import nl.in4392.models.DistributedProtocol._
import nl.in4392.models.DistributedProtocol.TaskCompleted
import nl.in4392.models.DistributedProtocol.WorkerRegister
import nl.in4392.models.DistributedProtocol.WorkerRequestTask
import nl.in4392.models.Task._

import akka.actor.{ ActorRef, Props, Actor, ActorSystem }

import nl.in4392.models.Task.Task
import nl.in4392.models.WorkerStatusProtocol.WorkerState
import nl.in4392.models.WorkerStatusProtocol.Working
import nl.in4392.models.WorkerStatusProtocol.{Working, Idle, WorkerState}
import scala.collection.immutable.Queue




class MasterActor extends Actor with ActorLogging {




  private var jobQueue = Queue[Task]()
  private var workers = Map.empty[String,WorkerState]

  def receive = {

    case WorkerRegister(workerId) =>
      if (!workers.contains(workerId)) {
        workers += (workerId -> WorkerState(sender, status = Idle))
        println("Registered Worker: {}",workerId)

        if(!jobQueue.isEmpty)
          sender ! TaskAvailable
      }

    //http://stackoverflow.com/questions/10433539/how-to-use-a-map-value-in-a-match-case-statement-in-scala
    case WorkerRequestTask(workerId) =>
      workers.get(workerId) match {
        case Some(value @ WorkerState(_,Idle)) =>               //idomatic scala, we are not sure if the key is present (we could technically remove the contains statement above),
          if(!jobQueue.isEmpty) jobQueue.dequeue match {
            case(x,xs) =>
              jobQueue = xs
              workers += (workerId -> value.copy(status = Working(x)))        //dunno why copy is used, but it was the only option apparently, i just wanted to update
              println("The following task {} is handled by worker {}",x.job,workerId)
              sender ! x
            case _ =>
          }
        case Some(WorkerState(_,Working(_))) => println("The worker {} still working on another task",workerId)
        case _ => None
      }
    case TaskCompleted(workerId,taskId,result) =>
      workers.get(workerId) match {
        case Some(value @ WorkerState(_,Working(task))) =>
          if (task.taskId == taskId){
            log.debug("Task {} is completed by worker {}",taskId,workerId)
            workers += (workerId  -> value.copy(status=Idle))
            println("The result is {}",result.toString)         //here we need to present the result to the webinterface
            //add some ack
          }
        case _ => println("[Master][TaskCompleted] I dunno how I came here")
      }
    case TaskFailed(workerId,taskId) =>
      workers.get(workerId) match {
        case Some(value @ WorkerState(_,Working(task))) =>
          if (task.taskId == taskId){
            log.debug("Task {} failed by worker {}",taskId,workerId)
            workers += (workerId  -> value.copy(status=Idle))  //maybe not the best way to do, since we should investigate the error (we need to send some standard error cases)
            jobQueue = jobQueue enqueue task
            notifyWorkers() //assign this task to another work
          }
        case _ =>  println("[Master][TaskFailed] I dunno how I came here")
      }

    case task: Task =>
      println("Received task: {}", task)
      jobQueue = jobQueue enqueue task
      notifyWorkers()



  }
  def notifyWorkers(): Unit = {
    if (jobQueue.nonEmpty) {
      workers.foreach {
        case (_, WorkerState(worker,Idle)) =>
          worker ! TaskAvailable
        case _ => println("[Master][NotifyWorker] I dunno how I came here")
      }
    }
  }




}
