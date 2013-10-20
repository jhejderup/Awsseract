package controllers;

import play.api.mvc._
import play.api.libs.concurrent.Akka
import akka.actor.{Props}
import scala.concurrent.duration._
import play.api.Play.current
import play.api.libs.EventSource
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext
import models.{StartConvert, TextFeed}

object Actors {
  lazy val notifier = Akka.system.actorOf(Props[MainConvertActor])
}
object Application extends Controller {


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
      import java.io.File
      val filename = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(new File("/Users/jhejderup/Desktop/Reviews/"+picture.filename),true)
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.Application.upload).flashing(
        "error" -> "Missing file"
      )
    }
  }
//http://alvinalexander.com/scala/scala-akka-actors-ask-examples-future-await-timeout-result
//http://www.playframework.com/documentation/2.0/ScalaAkka
//http://www.playframework.com/documentation/2.1.x/ScalaStream
  def convert(image: File) = Action {
      Async {
        (Actors.notifier ? StartConvert(image = image)).map {
          case TextFeed(out) => Ok.stream(out &> EventSource()).as("text/event-stream")
        }
      }
    }

}