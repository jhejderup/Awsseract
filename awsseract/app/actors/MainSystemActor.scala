package actors

import akka.actor.{Actor,ActorLogging,Props, ActorSystem}
import models.ConvertProtocol.{ResultConvert, StartConvert}
import com.typesafe.config.ConfigFactory
import models.Task.Task
import scala.collection.mutable.HashMap
import java.util.UUID
import play.api.libs.concurrent.Akka
import play.api.Play.current
class MainSystemActor extends Actor with ActorLogging {
 /*
  private val system = ActorSystem("MasterNode", ConfigFactory.load().getConfig("masterSys"))
  private val masterActor = system.actorOf(Props[MasterActor], "masterActor")
   */
  // private var tasks = new HashMap[UUID, ]    keep track of tasks and execution time, etc
  def receive = {
    case StartConvert(taskId,image) => sender ! new Task(taskId.toString,image)


  }


}

