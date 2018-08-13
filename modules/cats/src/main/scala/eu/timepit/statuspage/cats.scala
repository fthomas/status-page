package eu.timepit.statuspage

import _root_.cats.ApplicativeError
import _root_.cats.instances.list._
import _root_.cats.syntax.applicativeError._
import _root_.cats.syntax.functor._
import _root_.cats.syntax.traverse._
import eu.timepit.statuspage.core.Item.{Entry, Group}
import eu.timepit.statuspage.core.Result.{Error, Info, Ok}
import eu.timepit.statuspage.core.{overallOf, Item, Result, Root}

object cats {

  final class Make[F[_], E](val show: E => String)(implicit F: ApplicativeError[F, E]) {

    def root(items: F[Item]*): F[Root] =
      items.toList.sequence.map(xs => Root(xs, overallOf(xs)))

    def group(name: String, items: F[Item]*): F[Item] =
      items.toList.sequence.map(xs => Group(name, xs, overallOf(xs)))

    def entry(name: String, f: F[Option[String]]): F[Item] =
      result(f).map(result => Entry(name, result))

    def result(f: F[Option[String]]): F[Result] =
      f.attempt.map(resultFromEither)

    def resultFromEither(either: Either[E, Option[String]]): Result =
      either match {
        case Right(None)          => Ok
        case Right(Some(message)) => Info(message)
        case Left(e)              => Error(Some(show(e)))
      }

  }

}
