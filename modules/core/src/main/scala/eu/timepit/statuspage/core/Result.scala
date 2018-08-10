package eu.timepit.statuspage.core

sealed trait Result extends Product with Serializable
object Result {
  final case object Ok extends Result
  final case class Info(message: String) extends Result
  final case class Error(maybeMessage: Option[String]) extends Result
}
