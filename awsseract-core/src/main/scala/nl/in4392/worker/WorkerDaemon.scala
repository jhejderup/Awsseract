package main.scala.nl.in4392.worker

import com.typesafe.config.ConfigFactory
import akka.kernel.Bootable
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.actor.ActorPath
import java.util.UUID
import nl.tudelft.ec2interface.instancemanager._

class WorkerDaemon extends Bootable {


  val instanceInfo = new RemoteActorInfo().getInfoFromFile("conf/masterInfo")
  val workerId = instanceInfo.getSelfInstanceID()
  val config = ConfigFactory.load().getConfig("workerSys")
  val system = ActorSystem("WorkerNode", config)
  
  val workerActor = system.actorOf(Props(new WorkerActor(workerId,ActorPath.fromString(instanceInfo.getActorPath()))))
  val watchActor = system.actorOf(Props(new MonitorActor(workerId,ActorPath.fromString(instanceInfo.getActorPath()))))



  def startup() {
  }


  def shutdown() {
    system.shutdown()
  }
}

object WorkerApp {
  def main(args: Array[String]) {
    val app = new WorkerDaemon
    println("[WorkerNode] Started WorkerNode")
  }
}
