package eu.timepit.statuspage

import eu.timepit.statuspage.core.Item.{Entry, Group}
import eu.timepit.statuspage.core.Result.{Error, Info, Ok}

object core {

  final case class Root(items: List[Item], overall: Overall)

  final case class Overall(result: Result)

  sealed trait Item extends Product with Serializable
  object Item {
    final case class Group(name: String, items: List[Item], overall: Overall) extends Item
    final case class Entry(name: String, result: Result) extends Item
  }

  sealed trait Result extends Product with Serializable
  object Result {
    final case object Ok extends Result
    final case class Info(message: String) extends Result
    final case class Error(maybeMessage: Option[String]) extends Result
  }

  def rootAsPlainText(root: Root): String =
    (overallAsPlainText(root.overall) :: root.items.map(itemAsPlainText)).mkString("\n")

  def overallAsPlainText(overall: Overall): String =
    s"status: ${resultAsPlainText(overall.result)}"

  def itemAsPlainText(item: Item): String =
    item match {
      case Group(name, items, overall) =>
        (overallAsPlainText(overall) :: items.map(itemAsPlainText))
          .map(name + "_" + _)
          .mkString("\n")
      case Entry(name, result) => s"$name: ${resultAsPlainText(result)}"
    }

  def resultAsPlainText(result: Result): String =
    result match {
      case Ok                  => "OK"
      case Info(message)       => message
      case Error(maybeMessage) => "ERROR" + maybeMessage.fold("")(s => s" $s")
    }

}
