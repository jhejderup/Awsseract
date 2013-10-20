package models

import play.api.libs.json.{Json, JsValue}
import java.util.UUID
import java.io.File
import play.api.libs.iteratee.Enumerator

//http://engineering.klout.com/2013/01/iteratees-in-big-data-at-klout/
//http://sadache.tumblr.com/post/26784721867/is-socket-push-bytes-all-what-you-need-to-program
//http://www.playframework.com/documentation/2.1.1/api/scala/index.html#play.api.libs.iteratee.Concurrent$

case class TextEntry(data: JsValue) {
  def json_string = Json.stringify(data)
}

case class TextFeed(out: Enumerator[JsValue])

case class ConvertedImages(txtEntry: TextEntry, channel: List[UUID])


case class StartConvert(id: UUID = UUID.randomUUID(), image: File)

case class StopConvert(id: UUID)

