package main.scala.nl.in4392.worker


import nl.in4392.models.Task.TaskResult
import nl.in4392.models.Task._

import akka.actor.Actor
import java.io._
import scala.sys.process._


class JobExecutorActor extends Actor {
  def receive = {

    case Task(taskId,job)  => job match{
      case s: String =>
        //do tessearct stuff here
        val result = "Hi! I am not yet implemented. Come back another time! " + job
        sender ! TaskResult(taskId,result)
      case byte: Array[Byte] =>
        storeImage(byte,"output_img")
        val result = extractText("output_img")
        sender ! TaskResult(taskId,result)
    }
  }

  def storeImage(bytearray: Array[Byte],filename: String): Unit = {
    val out = new FileOutputStream(filename)
    out.write(bytearray)
    out.close()
  }

  def extractText(filename: String): String = Seq("ls").!!


}
