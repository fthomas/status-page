
object status {

  final case class Root(items: List[Item])

  ///

  sealed trait Item extends Product with Serializable

  object Item {
    final case class Group(name: String, items: List[Item]) extends Item
    final case class Entry(name: String, result: Result) extends Item
  }

  ///

  sealed trait Result extends Product with Serializable

  object Result {
    final case object Ok extends Result
    final case class Value(value: String) extends Result
    final case class Error(message: String) extends Result
  }

  ///

  final case class Prefixed[A](value: A, prefixes: List[String])

  def collectWarningsAndErros(items: List[Item]): List[Prefixed[Result]] = {
    @tailrec
    def loop(items: List[Prefixed[Item]], acc: List[Prefixed[Result]]): List[Prefixed[Result]] =
      items match {
        case h :: t =>
          h.value match {
            case Item.Group(name, subitems) =>
              loop(subitems.map(item => Prefixed(item, name :: h.prefixes)) ++ items, acc)
            case Item.Entry(name, result) => Prefixed(result, name :: h.prefixes) :: acc
            case Item.Info(_, _)          => acc
          }
        case Nil => acc
      }
    loop(items.map(item => Prefixed(item, Nil)), Nil)
  }

  def asPlainText(root: Root): String = ???

  /*
  https://github.com/futurice/backend-best-practices#application-monitoring
  
status: WARN Too few items in database
database_status: WARN Too few customers in database
database_customers: 378
database_items: 1
elastic_search_status: OK
elastic_search_shards: 20
   */

  Root(
    List(
      Group(
        "database",
        List(
          Entry("status", Error("Too few customers in database")),
          Entry("customers", Value("378")),
          Entry("items", Value("1"))
        )
      )
    ))

}
