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

import eu.timepit.statuspage.core.Result.Ok

final case class Root(items: List[Item], result: Result)

sealed trait Item extends Product with Serializable {
  def result: Result
}

object Item {
  final case class Group(name: String, items: List[Item], result: Result) extends Item

  final case class Entry(name: String, result: Result) extends Item

  /** An [[Item]] which wraps another [[Item]] but always returns
    * [[Result.Ok Ok]] as [[Result]].
    *
    * This can be used to render the wrapped [[Item]] but ignoring its status
    * for the overall result.
    */
  final case class JustShow(wrapped: Item) extends Item {
    override def result: Result = Ok
  }
}
