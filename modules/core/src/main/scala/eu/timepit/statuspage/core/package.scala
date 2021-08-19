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

import eu.timepit.statuspage.core.Result.{Error, Info, Ok, Warning}

import scala.annotation.tailrec

package object core {

  /** Computes the overall result of `items`.
    *
    * Returns
    *   - `Ok` if `items` contains no `Warning` or `Error`
    *   - `Warning` if `items` contains at least one `Warning` and no `Error`
    *   - `Error` if `items` contains at least one `Error`
    */
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
