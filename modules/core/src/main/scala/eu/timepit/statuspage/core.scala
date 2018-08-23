/*
 * Copyright 2018 status-page contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.timepit.statuspage

import eu.timepit.statuspage.core.Item.{Entry, Group}
import eu.timepit.statuspage.core.Result.{Error, Info, Ok, Warning}

import scala.annotation.tailrec

object core {

  /// data structures

  final case class Root(items: List[Item], result: Result)

  sealed trait Item extends Product with Serializable {
    def result: Result
  }

  object Item {
    final case class Group(name: String, items: List[Item], result: Result) extends Item
    final case class Entry(name: String, result: Result) extends Item
  }

  sealed trait Result extends Product with Serializable
  object Result {
    final case object Ok extends Result
    final case class Info(message: String) extends Result
    final case class Warning(maybeMessage: Option[String]) extends Result
    final case class Error(maybeMessage: Option[String]) extends Result

    object Warning {
      def withMsg(message: String): Warning = Warning(Some(message))
      val withoutMsg: Warning = Warning(None)
    }

    object Error {
      def withMsg(message: String): Error = Error(Some(message))
      val withoutMsg: Error = Error(None)
    }
  }

  /// functions

  def rootAsPlainText(root: Root): String =
    (overallAsPlainText(root.result) :: root.items.map(itemAsPlainText)).mkString("\n")

  private def overallAsPlainText(overall: Result): String =
    s"status: ${resultAsPlainText(overall)}"

  private def itemAsPlainText(item: Item): String =
    item match {
      case Group(name, items, overall) =>
        (overallAsPlainText(overall) :: items.map(itemAsPlainText))
          .map(name + "_" + _)
          .mkString("\n")
      case Entry(name, result) => s"$name: ${resultAsPlainText(result)}"
    }

  private def resultAsPlainText(result: Result): String =
    result match {
      case Ok                    => "OK"
      case Info(message)         => message
      case Warning(maybeMessage) => appendWithSpace("WARNING", maybeMessage)
      case Error(maybeMessage)   => appendWithSpace("ERROR", maybeMessage)
    }

  private def appendWithSpace(fst: String, maybeSnd: Option[String]): String =
    maybeSnd.fold(fst)(snd => fst + " " + snd)

  def overallOf(items: List[Item]): Result = {
    @tailrec
    def loop(items: List[Item], acc: Result): Result =
      items match {
        case x :: xs =>
          x.result match {
            case Ok         => loop(xs, acc)
            case Info(_)    => loop(xs, acc)
            case Warning(_) => loop(xs, Warning.withoutMsg)
            case Error(_)   => Error.withoutMsg
          }
        case Nil => acc
      }
    loop(items, Ok)
  }

}
