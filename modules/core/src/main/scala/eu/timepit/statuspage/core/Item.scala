package eu.timepit.statuspage.core

sealed trait Item extends Product with Serializable
object Item {
  final case class Group(name: String, items: List[Item]) extends Item
  final case class Entry(name: String, result: Result) extends Item
}
