package models


/**
 * The protocol is for defining the messaging protocol between
 * master and worker
 */
object DistributedProtocol {

  //Messages to Master from Workers
  case class WorkerRegister(workerId: String)
  case class WorkerRequestWork(workerId: String)
  //Work status
  case class WorkCompleted(workerId: String, workId: String, result: Any)
  case class WorkFailed(workerId: String, workId: String)


  // Messages from Master to Worker
  case class Ack(id: String)
  //Maybe something more here as well

}
