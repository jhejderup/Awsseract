package actors

/**
 * Created with IntelliJ IDEA.
 * User: jhejderup
 * Date: 2013-10-21
 * Time: 00:20
 * To change this template use File | Settings | File Templates.
 */

 import akka.actor.{Props, Actor}
 import play.api.libs.iteratee.{Concurrent}
 import models._
 import scala.collection.mutable.HashMap
 import java.util.UUID
 import play.api.libs.json.JsValue

 import scala.concurrent.duration._
 import models.StartConvert
 import models.ConvertedImages
 import models.StopConvert

 import scala.concurrent.ExecutionContext
 import ExecutionContext.Implicits.global


class MainConvertActor extends Actor{

    var channels = new HashMap[UUID, Concurrent.Channel[JsValue]]

    val OCRActor = context.system.actorOf(Props[OCRActor], "OCRActor")   //EC2 actor

    def receive = {
        case startConvert: StartConvert => sender ! TextFeed(startConversion(startConvert))
        case stopConvert: StopConvert => stopConverting(stopConvert)
        case convertedImages: ConvertedImages => broadcastResult(convertedImages)
        case TextEntry: TextEntry => OCRActor ! textEntry
      }
      private def broadcastResult(convImages: ConvertedImages) {
        convImages.channel.foreach {
          channels.get(_).map {
            _ push convertedImages.TextEntry.data
          }
        }
      }
      private def startConversion(startConvert: StartConvert) =
        Concurrent.unicast[JsValue](
          onStart = (c) => {
            channels += (startConvert.id -> c)
            OCRActor ! startConvert
          },
          onComplete = {
            self ! StopConvert(startConvert.id)
          },
          onError = (str, in) => {
            self ! StopConvert(startConvert.id)
          }
        ).onDoneEnumerating(
          callback = {
            self ! StopConvert(startConvert.id)
          }
        )
      private def stopConverting(stopConvert: StopConvert) {
        channels -= stopConvert.id
        OCRActor ! stopConvert
      }

}
