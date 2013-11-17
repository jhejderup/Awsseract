package nl.in4392.master


import akka.actor.Actor
import akka.actor.ActorLogging
import main.scala.nl.in4392.models.DistributedProtocol._

import main.scala.nl.in4392.models.Task._

import akka.actor.{ ActorRef, Props, Actor, ActorSystem, Terminated }


import main.scala.nl.in4392.models.WorkerStatusProtocol._
import scala.collection.immutable.Queue
import scala.collection.mutable.{Map,HashMap}
import main.scala.nl.in4392.models.DistributedProtocol.TaskCompleted
import main.scala.nl.in4392.models.DistributedProtocol.MonitorRegister
import main.scala.nl.in4392.models.WorkerStatusProtocol.Working
import main.scala.nl.in4392.models.WorkerStatusProtocol.WorkerState
import main.scala.nl.in4392.models.DistributedProtocol.WorkerRegister
import main.scala.nl.in4392.models.Task.Task
import main.scala.nl.in4392.models.DistributedProtocol.TaskFailed
import main.scala.nl.in4392.models.DistributedProtocol.WorkerRequestTask
import nl.tudelft.ec2interface.taskmonitor.TaskInfo
import java.sql.Timestamp
import nl.tudelft.ec2interface.logging.LogManager
import main.scala.nl.in4392.worker.JobExecutorActor
import main.scala.nl.in4392.master.InstanceManagerActor
import nl.tudelft.ec2interface.instancemanager._

class MasterActor extends Actor with ActorLogging {
  import nl.tudelft.ec2interface._
  import nl.tudelft.ec2interface.sysmonitor._
  import nl.tudelft.ec2interface.logging._

  val instanceId = new RemoteActorInfo().getInfoFromFile("conf/masterInfo").getSelfInstanceID

  private var jobQueue = Queue[Task]()
  private var workers = new HashMap[String,WorkerState]
  private var watchers = new HashMap[String,ActorRef]

  val system = ActorSystem("InstanceManager")
  val instancemanager = system.actorOf(Props[InstanceManagerActor], "insmanager")

  override def preStart() = {
    log.info("[MasterActor] Start InstanceManager")
    instancemanager ! StartInstanceManager

  }
  def receive = {


    case Terminated(workerPath) => None

    case RequestSystemStatus =>
      log.info("[MasterActor] Reported Queue information and worker states to sender")
      sender ! SystemStatus(jobQueue.size, workers)

    case MonitorRegister(workerId) =>
      if(!watchers.contains(workerId)){
        watchers += (workerId -> sender)
        log.info("[MasterActor] Registered Watcher: {}",workerId)
        context.watch(sender)
        sender ! RequestSystemInfo
      }

    case ReportSystemInfo(workerId,json) =>
      val uInfo = new SystemUsage().FromJson(json)
      log.info("[MasterActor] Received system info from worker {}",workerId)
      new LogManager().logSystemUsage(uInfo)

    case WorkerRegister(workerId) =>
      if (!workers.contains(workerId)) {
        workers += (workerId -> WorkerState(sender, status = Idle))
        log.info("[MasterActor] Registered Worker: {}",workerId)

        if(!jobQueue.isEmpty)
          sender ! TaskAvailable
      }

    case WorkerDeregister(workerId) =>
      if (workers.contains(workerId)) {
        workers = workers - workerId
        log.info("[MasterActor] Deregistered Worker: {}",workerId)

      }
    //http://stackoverflow.com/questions/10433539/how-to-use-a-map-value-in-a-match-case-statement-in-scala
    case WorkerRequestTask(workerId) =>
      workers.get(workerId) match {
        case Some(value @ WorkerState(_,Idle)) =>               //idomatic scala, we are not sure if the key is present (we could technically remove the contains statement above),
          if(!jobQueue.isEmpty) jobQueue.dequeue match {
            case(x,xs) =>
              jobQueue = xs
              workers += (workerId -> value.copy(status = Working(x)))
              var tInfo = new TaskInfo().FromJson(x.taskInfo)
              tInfo.setTransferTime(new Timestamp(System.currentTimeMillis()))
              sender ! new Task(x.taskId,x.job,new TaskInfo().ToJson(tInfo))
              log.info("[MasterActor] Task {} is send to worker {}",x.job,workerId)
            case _ =>
          }
        case Some(WorkerState(_,Working(_))) => log.info("[MasterActor] Worker {} is not Idle",workerId)
        case _ => None
      }
    case TaskCompleted(workerId,taskId,result,taskInfo) =>
      workers.get(workerId) match {
        case Some(value @ WorkerState(_,Working(task))) =>
          if (task.taskId == taskId){
            log.debug("Task {} is completed by worker {}",taskId,workerId)
            workers += (workerId  -> value.copy(status=Idle))
            new LogManager().logTask(new TaskInfo().FromJson(taskInfo))

          }
        case _ => None
      }
    case TaskFailed(workerId,taskId,taskInfo) =>
      workers.get(workerId) match {
        case Some(value @ WorkerState(_,Working(task))) =>
          if (task.taskId == taskId){
            log.debug("Task {} failure reported by worker {}",taskId,workerId, taskInfo.toString())
            workers += (workerId  -> value.copy(status=Idle))
            jobQueue = jobQueue enqueue task
            workerBroadcast() //assign this task to another work
          }
        case _ =>  None
      }
    case task: Task =>
      log.info("[MasterActor] Received task: {}", task.taskInfo.toString())
      var tInfo = new TaskInfo().FromJson(task.taskInfo)
      tInfo.setMasterId(instanceId)
      tInfo.setReceiveTime(new Timestamp(System.currentTimeMillis()))
      jobQueue = jobQueue enqueue new Task(task.taskId,task.job,new TaskInfo().ToJson(tInfo))
      workerBroadcast()
  }
  def workerBroadcast(): Unit = {
    if (jobQueue.nonEmpty) {
      workers.foreach {
        case (_, WorkerState(worker,Idle)) =>
          worker ! TaskAvailable
        case _ => None
      }
    }
  }




}
