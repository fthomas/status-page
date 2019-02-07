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

package eu.timepit.statuspage.core

import eu.timepit.statuspage.core.Item.{Entry, Group, JustShow}
import eu.timepit.statuspage.core.Result.{Error, Info, Ok, Warning}

object plaintext {
  def renderRoot(root: Root): String =
    (renderOverall(root.result) :: root.items.map(renderItem)).mkString("\n")

  def renderOverall(overall: Result): String =
    s"status: ${renderResult(overall)}"

  def renderItem(item: Item): String =
    item match {
      case Group(name, items, overall) =>
        (renderOverall(overall) :: items.map(renderItem))
          .map(name + "_" + _)
          .mkString("\n")
      case Entry(name, result) => s"$name: ${renderResult(result)}"
      case JustShow(wrapped)   => renderItem(wrapped)
    }

  def renderResult(result: Result): String =
    result match {
      case Ok                    => "OK"
      case Info(message)         => message
      case Warning(maybeMessage) => appendWithSpace("WARNING", maybeMessage)
      case Error(maybeMessage)   => appendWithSpace("ERROR", maybeMessage)
    }

  def appendWithSpace(fst: String, maybeSnd: Option[String]): String =
    maybeSnd.fold(fst)(snd => fst + " " + snd)
}
