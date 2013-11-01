package actors
import akka.actor.{Actor,ActorPath, ActorLogging, ActorRef}
import java.util.UUID
import actors.AWSTask.Task

/**
 * Created with IntelliJ IDEA.
 * User: jhejderup
 * Date: 2013-11-01
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */
/**
 * This actor has three behaviours 1) Idle 2) Working 3) Waiting for ack from master
 * @param masterPath
 */

object Worker {
  case class Task(taskId: String, job: Any)
  case class WorkComplete(result: Any)

}


class WorkerActor(masterPath: ActorPath) extends Actor with ActorLogging {
  import models.DistributedProtocol._
  import Worker._

  //master node
  val master = context.actorSelection(masterPath)
  //assigned worker id
  val workerId = UUID.randomUUID().toString
  //get the task executor actor
  val taskExecutor = context.watch(context.actorOf(workExecutorProps, "task-executer"))

  //During initialization, register at the master
  override def preStart() = master ! WorkerRegister(workerId)

  //http://doc.akka.io/docs/akka/1.3.1/scala/actors.html -> Extending Actors using PartialFUnction chaining

  def idleMessageHandler: Receive = {

    case WorkAvailable  =>  master ! WorkerRequestWork(workerId)
    case Task(taskId,job) =>
      taskExecutor ! job
      context.become(workingMessageHandler)
  }
  //implement ack some other time
  def workingMessageHandler(): Receive = {

    case WorkComplete(result) =>
      log.debug("Task Finished. Result {}.", result)
      master ! WorkCompleted(workerId,workId = "123",result)
      master ! WorkerRequestWork(workerId)
        context.become(idleMessageHandler)

    case _: Task =>
      log.info("Already working!")


  }



  def receive = idleMessageHandler






}
