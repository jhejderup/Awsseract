package models
import java.util.UUID

object ConvertProtocol {

  case class StartConvert(taskId: UUID=UUID.randomUUID(), image: Array[Byte])
  case class ResultConvert(taskId: UUID, text: String)

}
