package actors
import akka.actor.Actor
/**
 * Created with IntelliJ IDEA.
 * User: jhejderup
 * Date: 2013-11-01
 * Time: 00:54
 * To change this template use File | Settings | File Templates.
 */
class TaskExecutor extends Actor {

  def receive = {
    case _:_  =>
      //do tessearct stuff here
      val result = "Hi! I am not yet implemented. Come back another time! "
      sender ! Worker.WorkComplete(result)
  }


}
