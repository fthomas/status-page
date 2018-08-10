package eu.timepit.statuspage

import eu.timepit.statuspage.core.Item.{Entry, Group}
import eu.timepit.statuspage.core.Result.{Error, Info, Ok}

import scala.annotation.tailrec

package object core {
  def accumulatedResult(items: List[Item]): Result = {
    @tailrec
    def loop(items: List[Item], acc: Result): Result =
      items match {
        case x :: xs =>
          x match {
            case Entry(_, Error(_)) => Error(None)
            case _                  => loop(xs, acc)
          }
        case Nil => acc
      }
    loop(items, Ok)
  }

  def addStatusEntries(items: List[Item]): List[Item] = {
    val hasStatusEntry = items.exists {
      case Entry(`statusKey`, _) => true
      case _                     => false
    }
    if (hasStatusEntry) items
    else Entry(statusKey, accumulatedResult(items)) :: items
  }

  val statusKey: String = "status"
}
