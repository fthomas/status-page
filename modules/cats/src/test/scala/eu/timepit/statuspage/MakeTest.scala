package eu.timepit.statuspage

import _root_.cats.instances.either._
import eu.timepit.statuspage.cats._
import eu.timepit.statuspage.core.Result.{Error, Info}
import eu.timepit.statuspage.core.plaintext.renderRoot
import munit.FunSuite

class MakeTest extends FunSuite {
  type F[A] = Either[String, A]
  final val mk = new Make[F, String](identity)

  test("mk.root 1") {
    val obtained = mk
      .root(mk.entryOk("database", Right(0)), mk.entryOk("network", Right(0)))
      .map(renderRoot)
      .getOrElse("")
    val expected =
      s"""|status: OK
          |database: OK
          |network: OK
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("mk.root 2") {
    val msg = "Database is not accessible"
    val obtained = mk
      .root(mk.entryOk("database", Left(msg)), mk.entryOk("network", Right(0)))
      .map(renderRoot)
      .getOrElse("")
    val expected =
      s"""|status: ERROR
          |database: ERROR $msg
          |network: OK
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("mk.root 3") {
    val obtained = mk
      .root(
        mk.group(
          "database",
          mk.entryInfo("customers", Right("378")),
          mk.entryInfo("items", Right("8934748"))
        )
      )
      .map(renderRoot)
      .getOrElse("")
    val expected =
      s"""|status: OK
          |database_status: OK
          |database_customers: 378
          |database_items: 8934748
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("mk.root 4") {
    val obtained = mk
      .root(
        mk.group(
          "database",
          mk.entryInfo("customers", Right("378")),
          mk.entryInfo("items", Right("8934748"))
        ),
        mk.entryOk("network", Left("timeout"))
      )
      .map(renderRoot)
      .getOrElse("")
    val expected =
      s"""|status: ERROR
          |database_status: OK
          |database_customers: 378
          |database_items: 8934748
          |network: ERROR timeout
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("mk.root 5") {
    val obtained = mk
      .root(
        mk.entry("database_items", Right(8934748))(i =>
          if (i > 100) Info(i.toString)
          else Error.withMsg(i.toString)
        )
      )
      .map(renderRoot)
      .getOrElse("")
    val expected =
      s"""|status: OK
          |database_items: 8934748
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("mk.root 6") {
    val obtained = mk
      .root(
        mk.entry("database_items", Right(42))(i =>
          if (i > 100) Info(i.toString) else Error.withMsg(i.toString)
        )
      )
      .map(renderRoot)
      .getOrElse("")
    val expected =
      s"""|status: ERROR
          |database_items: ERROR 42
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }
}
