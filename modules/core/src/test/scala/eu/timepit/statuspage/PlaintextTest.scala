package eu.timepit.statuspage

import eu.timepit.statuspage.core.Item.{Entry, Group, JustShow}
import eu.timepit.statuspage.core.Result.{Error, Info, Ok, Warning}
import eu.timepit.statuspage.core._
import eu.timepit.statuspage.core.plaintext.renderRoot
import munit.FunSuite

class PlaintextTest extends FunSuite {
  test("renderRoot 1") {
    assertEquals(renderRoot(Root(Nil, Ok)), "status: OK")
  }

  test("renderRoot 2") {
    assertEquals(renderRoot(Root(Nil, Error.withoutMsg)), "status: ERROR")
  }

  test("renderRoot 3") {
    val msg = "Database is not accessible"
    assertEquals(renderRoot(Root(Nil, Error.withMsg(msg))), s"status: ERROR $msg")
  }

  test("renderRoot 4") {
    val obtained = renderRoot(Root(List(Entry("database_status", Ok)), Ok))
    val expected = s"""|status: OK
          |database_status: OK
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("renderRoot 5") {
    val obtained =
      renderRoot(Root(List(Entry("database_status", Ok), Entry("elastic_search_status", Ok)), Ok))
    val expected =
      s"""|status: OK
          |database_status: OK
          |elastic_search_status: OK
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("renderRoot 6") {
    val obtained = renderRoot(
      Root(
        List(
          Group(
            "database",
            List(Entry("customers", Info("378")), Entry("items", Info("8934748"))),
            Ok
          )
        ),
        Ok
      )
    )
    val expected =
      s"""|status: OK
          |database_status: OK
          |database_customers: 378
          |database_items: 8934748
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("renderRoot 7") {
    val obtained = renderRoot(
      Root(List(Group("database", List(Group("node1", Nil, Ok), Group("node2", Nil, Ok)), Ok)), Ok)
    )
    val expected =
      s"""|status: OK
          |database_status: OK
          |database_node1_status: OK
          |database_node2_status: OK
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("renderRoot 8") {
    val items = List(Entry("database1", Ok), Entry("database2", Warning.withMsg("slow")))
    val obtained = renderRoot(Root(items, overallOf(items)))
    val expected =
      s"""|status: WARNING
          |database1: OK
          |database2: WARNING slow
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("renderRoot 9") {
    val items = List(
      Entry("database1", Ok),
      Entry("database2", Warning.withMsg("slow")),
      Entry("database3", Error.withMsg("unavailable"))
    )
    val obtained = renderRoot(Root(items, overallOf(items)))
    val expected =
      s"""|status: ERROR
          |database1: OK
          |database2: WARNING slow
          |database3: ERROR unavailable
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }

  test("renderRoot: JustShow") {
    val items =
      List(Entry("entry1", Ok), JustShow(Entry("entry2", Error.withoutMsg)), Entry("entry3", Ok))
    val obtained = renderRoot(Root(items, overallOf(items)))
    val expected =
      s"""|status: OK
          |entry1: OK
          |entry2: ERROR
          |entry3: OK
       """.stripMargin.trim
    assertEquals(obtained, expected)
  }
}
