package controllers;

import play.api.mvc._
import play.api.libs.concurrent.Akka
import play.libs.Akka._
import scala.concurrent.duration._
import play.api.Play.current
import play.api.libs.EventSource
import scala.concurrent.ExecutionContext

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


}