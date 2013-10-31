package actors
import akka.actor.{Actor, ActorLogging, ActorRef}
import scala.collection.immutable.Queue
/**
 * Created with IntelliJ IDEA.
 * User: jhejderup
 * Date: 2013-10-31
 * Time: 09:53
 * To change this template use File | Settings | File Templates.
 */
//collection operations like Map: http://www.scala-lang.org/docu/files/collections-api/collections_10.html

// This book is a life-saver: http://www.cs.ucsb.edu/~benh/162/Programming-in-Scala.pdf
object Master {

  import AWSTask.Task

  trait StatusWorker
  case class Working(task: Task) extends StatusWorker
  case object Idle extends StatusWorker
  case class StateWorker(ref: ActorRef, status: StatusWorker)

}

object AWSTask {

  case class Task(taskId: String, workerId: String, job: Any)
  case class TaskResult(taskId: String, result: Any)


}
class MasterActor extends Actor with ActorLogging {
  import models.DistributedProtocol._
  import Master._
  import AWSTask._
  private trait WorkerStatus

  private var workers = Map.empty[String, StateWorker]
  private var pendingQueue = Queue[Task]()  //we can implement a priorityqueue at a later stage

  def receive = {

    //TODO: Have a mechanism in case a worker wants to register when it already has done that
    //TODO: Since we use UUID as key in the map, it could potentially be that two different worker has the same
    case WorkerRegister(workerId) =>
      if (!workers.contains(workerId)) {
        workers += (workerId -> StateWorker(sender, status = Idle))      //this is how you add a key-value pair in scala:  key -> value
        log.debug("Registered Worker: {}",workerId)

        if(!pendingQueue.isEmpty)
          sender ! WorkAvailable
      }
     //http://stackoverflow.com/questions/10433539/how-to-use-a-map-value-in-a-match-case-statement-in-scala
    case WorkerRequestWork(workerId) =>
      if (workers.contains(workerId)) {
        workers.get(workerId) match {
          case Some(value @ StateWorker(_,Idle)) =>               //idomatic scala, we are not sure if the key is present (we could technically remove the contains statement above),
            if(!pendingQueue.isEmpty) pendingQueue.dequeue match {
              case(x,xs) =>
                pendingQueue = xs
                workers += (workerId -> value.copy(status = Working(x)))        //dunno why copy is used, but it was the only option apparently, i just wanted to update
                log.debug("The following task {} is handled by worker {}",x.job,workerId)
                sender ! x
              case _ =>
            }
          case Some(StateWorker(_,Working(_,_))) => log.debug("The worker {} still working on another task",workerId)
          case _ => None
        }
      }
    //the worker reports that the task is successfully done
    case WorkCompleted(workerId,taskId,result) =>
      workers.get(workerId) match {
        case Some(value @ StateWorker(_,Working(task,_))) =>
          if (task.tasxkId == taskId){
            log.debug(x"Task {} is completed by worker {}",taskId,workerId)
            workers +=x (workerId  -> value.copy(status=Idle))
            log.info("The result is {}",result.toString)         //here we need to present the result to the webinterface
            sender ! Ack(taskId)
          }
        case _ =>
      }
    case WorkFailed(workerId,taskId) =>
     workers.get(workerId) match {
       case Some(value @ StateWorker(_,Working(task,_))) =>
         if (task.taskId == taskId){
           log.debug("Task {} failed by worker {}",taskId,workerId)
           workers += (workerId  -> value.copy(status=Idle))  //maybe not the best way to do, since we should investigate the error (we need to send some standard error cases)
           pendingQueue = pendingQueue enqueue task
           notifyWorkers() //assign this task to another work
         }
       case _ =>
     }
    case task: Task =>
      log.debug("Received task: {}", task)
      pendingQueue = pendingQueue enqueue task
      notifyWorkers()

  }

  //Inform idle workers that work is available and they cant be lazy!!
  def notifyWorkers(): Unit = {
    if (pendingQueue.nonEmpty) {
          workers.foreach {
            case (worker, m)
               if (m.isEmpty) => worker ! WorkAvailable
            case _ =>
          }
    }
  }



}
