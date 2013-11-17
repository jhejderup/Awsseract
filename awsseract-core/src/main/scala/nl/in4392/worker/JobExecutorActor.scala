package main.scala.nl.in4392.worker


import main.scala.nl.in4392.models.Task.TaskResult
import main.scala.nl.in4392.models.Task._

import akka.actor.Actor
import java.io._
import scala.sys.process._


class JobExecutorActor extends Actor with ActorLogging {

  override def postRestart(err:Throwable) = {
    log.info("[JobExecutorActor] Restart after failure (continuing from the state before the exception)")
  }
  def receive = {

    case Task(taskId,job,taskInfo)  => job match{
      case byte: Array[Byte] =>
        log.info("[JobExecutorActor] Received task")
        storeImage(byte,"output_img")
        val result = extractText("output_img")
        sender ! TaskResult(taskId,result,taskInfo)
      case _ => log.info("[JobExecutorActor] Unknown task")
    }
  }

  def storeImage(bytearray: Array[Byte],filename: String): Unit = {
    val out = new FileOutputStream(filename)
    out.write(bytearray)
    out.close()
  }
  def extractText(filename: String)={
    if(Seq("tesseract",filename,"output","-l","eng").! == 0)
      Seq("cat","output.txt").!!
  }


}
