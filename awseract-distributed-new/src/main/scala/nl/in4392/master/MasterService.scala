package main.scala.nl.in4392.master

import nl.in4392.master.MasterActor
import com.typesafe.config.ConfigFactory
import akka.kernel.Bootable
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import nl.in4392.models.Task.Task
import java.io._
class MasterService extends Bootable {

  val system = ActorSystem("MasterNode", ConfigFactory.load().getConfig("masterSys"))
  val masterActor = system.actorOf(Props[MasterActor], name = "masterActor")


  def startup() {
  }

  def testTasks(): Unit = {

    masterActor ! new Task("aws-task-1","Hello Daniel! I am the first task")

    val bis = new BufferedInputStream(new FileInputStream("/Users/jhejderup/Workspace/Scala/awseract-distributed/src/main/resources/TEST_2.JPG"))
    val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray

    //val inputStream = new FileInputStream(imageFile)
    masterActor ! new Task("aws-task-2",byteArray)
  }


  def shutdown() {
    system.shutdown()
  }
}

object MasterApp {
  def main(args: Array[String]) {
    val app = new MasterService
    println("Started Master Node")

    app.testTasks()
  }
}