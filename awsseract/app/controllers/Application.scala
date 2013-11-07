package controllers;

import play.api.mvc._
import play.api.libs.concurrent.Akka
import akka.actor.{Props, ActorSystem}
import scala.concurrent.duration._
import play.api.Play.current
import actors._
import play.api.libs.EventSource
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.{Await, Future, ExecutionContext}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import java.io.{FileInputStream, BufferedInputStream}
import models.ConvertProtocol.{ResultConvert, StartConvert}
import akka.dispatch.Futures._
import akka.pattern.{ ask, pipe }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.duration._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import scala.concurrent.ExecutionContext
import com.typesafe.config.ConfigFactory
import play.api.Play.current
import nl.in4392.models.Task._
import java.util.UUID
import scala.util.{ Success, Failure }
import nl.in4392.models.DistributedProtocol.TaskCompleted

object Application extends Controller {

  //private val system = ActorSystem("MainSystem")
  //val mainSysActor = system.actorOf(Props[MainSystemActor], "mainsysActor")

  // val mainSysActor = Akka.system.actorOf(Props[MainSystemActor],"mainsysActor")
  private val system = ActorSystem("MasterNode", ConfigFactory.load().getConfig("masterSys"))
  private val masterActor = system.actorOf(Props[MasterActor], "masterActor")
  def index = Action {
    Ok(views.html.index("Your new application is ready"))
  }

  def upload = Action {
    Ok(views.html.upload("Your new application is ready"))
  }

  def statistics = Action {
    Ok(views.html.statistics("Your new application is ready"))
  }

  def about = Action{
    Ok(views.html.about("Your new application is ready"))

  }

  //still under testing, change the path for saving the image to a local absolute path
  //TODO: mechanism for determing the file type (to save performacen/energy) try to find on client side
  def uploadFile = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { picture =>
      val bis = new BufferedInputStream(new FileInputStream(picture.ref.file))
      val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
      convertImage(byteArray)
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.Application.upload).flashing(
        "error" -> "Missing file"
      )
    }
  }

  /*
  def uploadFile = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { picture =>
      import java.io.File
      val bis = new BufferedInputStream(new FileInputStream(picture.filename))
      val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
      val contentType = picture.contentType
      convertImage(byteArray)
    }.getOrElse {
      Redirect(routes.Application.upload).flashing(
        "error" -> "Missing file"
      )
    }
  }
  */

  def convertImage(imageRaw: Array[Byte]) = {
    println("inside convert image")
    implicit val timeout = Timeout(20 seconds) // needed for `?` below
    val resultFuture = masterActor ? new Task(UUID.randomUUID().toString,imageRaw)
    resultFuture onComplete {
      case Success(Task(taskid,result)) =>
        println("tillbaka i master, id"+taskid+"resultat "+result)
      case Failure(t) =>
        t.printStackTrace()
    }
  }

  /* implicit val timeout = Timeout(50 seconds) // needed for `?` below
    print("hello")
    val convertFuture = scala.concurrent.Future {mainSysActor ? StartConvert(image = imageRaw)}
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 50.second)
    Future.firstCompletedOf(Seq(convertFuture, timeoutFuture)).map {
      case ResultConvert(taskId,text) =>
        println("Got result"+text)
        Ok("Got result: " + text)
      case t: String => InternalServerError(t)
    }
    */




//http://alvinalexander.com/scala/scala-akka-actors-ask-examples-future-await-timeout-result
//http://www.playframework.com/documentation/2.0/ScalaAkka
//http://www.playframework.com/documentation/2.1.x/ScalaStream
  /*
  def convert(image: File) = Action {
      Async {
        (Actors.notifier ? StartConvert(image = image)).map {
          case TextFeed(out) => Ok.stream(out &> EventSource()).as("text/event-stream")
        }
      }
    }
    */


}

