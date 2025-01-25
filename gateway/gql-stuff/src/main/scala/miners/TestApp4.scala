package ru.sskie.vpered.gql
package miners

import caliban.CalibanError.ValidationError
import caliban._
import caliban.schema.Schema.auto._
import caliban.schema.ArgBuilder.auto._
import zio.{IO, ZIO, ZIOAppDefault}

object TestApp4 extends ZIOAppDefault {

  val test4: IO[ValidationError, Int] =
    for {
      dbService   <- DBService()
      resolver     = Api4.resolver(dbService)
      api          = graphQL(RootResolver(resolver))
      interpreter <- api.interpreter
      sth         <- interpreter.execute(Query.orders)
      _            = println(sth.data.toString)
      dbHits      <- dbService.hits
      _           <- ZIO.debug(s"ZQuery with Batch - DB Hits: $dbHits")
    } yield 0

  override def run: IO[ValidationError, Int] = test4
}
