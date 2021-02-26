# status-page
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/fthomas/status-page/Continuous%20Integration)](https://github.com/fthomas/status-page/actions?query=workflow%3A%22Continuous+Integration%22)
[![codecov](https://codecov.io/gh/fthomas/status-page/branch/master/graph/badge.svg)](https://codecov.io/gh/fthomas/status-page)
[![Scaladex](https://index.scala-lang.org/fthomas/status-page/latest.svg?color=blue)](https://index.scala-lang.org/fthomas/status-page/status-page-core)
[![Scaladoc](https://www.javadoc.io/badge/eu.timepit/status-page-core_2.12.svg?color=blue&label=Scaladoc)](https://javadoc.io/doc/eu.timepit/status-page-core_2.12)

**status-page** is a microlibrary for creating simple status pages.
It provides data structures and functions to organize, aggregate, and render
status information in a straightforward way.

## Quick example

```scala
import cats.effect.IO
import eu.timepit.statuspage.cats.Make
import eu.timepit.statuspage.core.Result.{Ok, Warning}
import eu.timepit.statuspage.core.plaintext

// We use `IO` values for status checks, but the library supports any
// type that is an `ApplicativeError`.
val uptime: IO[String] = IO("up 2 weeks, 3 days, 13 hours, 27 minutes")
val dbQuery: IO[Unit] = IO(())
val dbItems: IO[Int] = IO(38)
val sparkNode1: IO[Unit] = IO(())
val sparkNode2: IO[Unit] = IO.raiseError(new Exception("unreachable"))
```
```scala
val mk = new Make[IO, Throwable](_.getMessage)
// mk: eu.timepit.statuspage.cats.Make[cats.effect.IO,Throwable] = eu.timepit.statuspage.cats.Make@12af9bd3

val root = mk.root(
  mk.entryInfo("uptime", uptime),
  mk.group(
    "database",
    mk.entryOk("query", dbQuery),
    mk.entry("items", dbItems)(i => if (i > 50) Ok else Warning.withMsg(i.toString))
  ),
  mk.group(
    "spark_cluster",
    mk.entryOk("node1", sparkNode1),
    mk.entryOk("node2", sparkNode2)
  )
)
// root: cats.effect.IO[eu.timepit.statuspage.core.Root] = <function1>

root.map(plaintext.renderRoot).unsafeRunSync()
// res0: String =
// status: ERROR
// uptime: up 2 weeks, 3 days, 13 hours, 27 minutes
// database_status: WARNING
// database_query: OK
// database_items: WARNING 38
// spark_cluster_status: ERROR
// spark_cluster_node1: OK
// spark_cluster_node2: ERROR unreachable
```

## Using status-page

The latest version of the library is available for Scala 2.12, 2.13, and 3.

If you're using sbt, add the following to your build:
```sbt
libraryDependencies ++= Seq(
  "eu.timepit" %% "status-page-core" % "<latestVersion>",
  "eu.timepit" %% "status-page-cats" % "<latestVersion>"
)
```

## License

**status-page** is licensed under the Apache License, Version 2.0, available at
http://www.apache.org/licenses/LICENSE-2.0 and also in the
[LICENSE](https://github.com/fthomas/status-page/blob/master/LICENSE) file.
