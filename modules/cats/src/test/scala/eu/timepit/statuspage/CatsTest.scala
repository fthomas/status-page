package eu.timepit.statuspage

import _root_.cats.instances.either._
import eu.timepit.statuspage.cats._
import eu.timepit.statuspage.core.Result.{Error, Info}
import eu.timepit.statuspage.core.rootAsPlainText
import org.scalatest.{FunSuite, Matchers}

class CatsTest extends FunSuite with Matchers {
  type F[A] = Either[String, A]
  final val mk = new Make[F, String](identity)

  test("mk.root 1") {
    mk.root(mk.entry("database", Right(None)), mk.entry("network", Right(None)))
      .map(rootAsPlainText)
      .getOrElse("") shouldBe
      s"""|status: OK
          |database: OK
          |network: OK
       """.stripMargin.trim
  }

  test("mk.root 2") {
    val msg = "Database is not accessible"
    mk.root(mk.entry("database", Left(msg)), mk.entry("network", Right(None)))
      .map(rootAsPlainText)
      .getOrElse("") shouldBe
      s"""|status: ERROR
          |database: ERROR $msg
          |network: OK
       """.stripMargin.trim
  }

  test("mk.root 3") {
    mk.root(
        mk.group(
          "database",
          mk.entry("customers", Right(Some("378"))),
          mk.entry("items", Right(Some("8934748")))))
      .map(rootAsPlainText)
      .getOrElse("") shouldBe
      s"""|status: OK
          |database_status: OK
          |database_customers: 378
          |database_items: 8934748
       """.stripMargin.trim
  }

  test("mk.root 4") {
    mk.root(
        mk.group(
          "database",
          mk.entry("customers", Right(Some("378"))),
          mk.entry("items", Right(Some("8934748")))),
        mk.entry("network", Left("timeout")))
      .map(rootAsPlainText)
      .getOrElse("") shouldBe
      s"""|status: ERROR
          |database_status: OK
          |database_customers: 378
          |database_items: 8934748
          |network: ERROR timeout
       """.stripMargin.trim
  }

  test("mk.root 5") {
    mk.root(mk.entryF("database_items", Right(8934748))(i =>
        if (i > 100) Info(i.toString) else Error(Some(i.toString))))
      .map(rootAsPlainText)
      .getOrElse("") shouldBe
      s"""|status: OK
          |database_items: 8934748
       """.stripMargin.trim
  }

  test("mk.root 6") {
    mk.root(mk.entryF("database_items", Right(42))(i =>
        if (i > 100) Info(i.toString) else Error(Some(i.toString))))
      .map(rootAsPlainText)
      .getOrElse("") shouldBe
      s"""|status: ERROR
          |database_items: ERROR 42
       """.stripMargin.trim
  }
}
