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

package eu.timepit.statuspage.cats

import _root_.cats.ApplicativeError
import _root_.cats.instances.list._
import _root_.cats.syntax.applicativeError._
import _root_.cats.syntax.functor._
import _root_.cats.syntax.traverse._
import eu.timepit.statuspage.core.Item.{Entry, Group}
import eu.timepit.statuspage.core.Result.{Error, Info, Ok}
import eu.timepit.statuspage.core.{overallOf, Item, Result, Root}

final class Make[F[_], E](val showError: E => String)(implicit F: ApplicativeError[F, E]) {

  def root(items: F[Item]*): F[Root] =
    items.toList.sequence.map(xs => Root(xs, overallOf(xs)))

  def group(name: String, items: F[Item]*): F[Item] =
    items.toList.sequence.map(xs => Group(name, xs, overallOf(xs)))

  def entry[A](name: String, fa: F[A])(f: A => Result): F[Item] =
    fa.attempt.map(ea => Entry(name, resultFromEither(ea)(f)))

  def entryOk[A](name: String, fa: F[A]): F[Item] =
    entry(name, fa)(_ => Ok)

  def entryInfo(name: String, fmsg: F[String]): F[Item] =
    entry(name, fmsg)(msg => Info(msg))

  def resultFromEither[A](either: Either[E, A])(f: A => Result): Result =
    either.fold(resultFromError, f)

  def resultFromError(e: E): Result =
    Error.withMsg(showError(e))

}
