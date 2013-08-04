package rovachan

object Context {
  implicit val DefaultContext = scala.concurrent.ExecutionContext.global
}
