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

sealed trait Result extends Product with Serializable

object Result {
  case object Ok extends Result
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
