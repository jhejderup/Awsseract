package main.scala.nl.in4392.master

import nl.in4392.master.MasterActor
import com.typesafe.config.ConfigFactory
import akka.kernel.Bootable
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import main.scala.nl.in4392.models.Task.Task
import java.io._
import java.util.UUID
import nl.tudelft.ec2interface.taskmonitor.TaskInfo

class MasterService extends Bootable {

  val system = ActorSystem("MasterNode", ConfigFactory.load().getConfig("masterSys"))
  val masterActor = system.actorOf(Props[MasterActor], name = "masterActor")


  def startup() {
  }


  def shutdown() {
    system.shutdown()
  }
}

object MasterApp {
  def main(args: Array[String]) {
    val app = new MasterService
    println("[WorkerNode] Started Master Node")

  }
}
