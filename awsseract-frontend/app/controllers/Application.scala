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
import play.api.libs.EventSource
import play.api.libs.json.JsValue
import play.api.libs.iteratee.Concurrent
import play.libs.Json

object Application extends Controller {

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

  def uploadFile = Action(parse.multipartFormData) { request =>
    request.body.file("file").map { picture =>
      val bis = new BufferedInputStream(new FileInputStream(picture.ref.file))
      val byteArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
      convertImage(byteArray)

    }.getOrElse {
      Redirect(routes.Application.upload).flashing(
        "error" -> "Missing file"
      )
    }

  }

  def convertImage(imageRaw: Array[Byte]) = {
    println("inside convert image")
    masterActor ! new Task(UUID.randomUUID().toString,imageRaw)     //Send task to master actor

  }


}

