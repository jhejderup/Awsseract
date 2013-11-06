package main.scala.nl.in4392.worker

import com.typesafe.config.ConfigFactory
import akka.kernel.Bootable
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import akka.actor.ActorPath
class WorkerDaemon extends Bootable {

  val config = ConfigFactory.load().getConfig("workerSys")
  val system = ActorSystem("WorkerNode", config)
  val workerActor = system.actorOf(Props(new WorkerActor(ActorPath.fromString("akka.tcp://MasterNode@127.0.0.1:2000/user/masterActor"))))




  def startup() {
  }


  def shutdown() {
    system.shutdown()
  }
}

object WorkerApp {
  def main(args: Array[String]) {
    val app = new WorkerDaemon
    println("Started Worker Node")
  }
}