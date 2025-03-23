package ru.sskie.vpered.gql
package tests

import caliban.CalibanError.ValidationError
import caliban._
import caliban.schema.Schema.auto._
import caliban.schema.ArgBuilder.auto._
import zio.{IO, ZIO, ZIOAppDefault}

object TestApp2 extends ZIOAppDefault {

  val test2: IO[ValidationError, Int] =
    for {
      dbService   <- DBService()
      resolver     = Api2.resolver(dbService)
      api          = graphQL(RootResolver(resolver))
      interpreter <- api.interpreter
      res         <- interpreter.execute(Query.orders)
//      res         <- interpreter.execute(Query.ordersWithBrands)
      dbHits      <- dbService.hits
      _           <- ZIO.debug(s"Nested Effects - DB Hits: $dbHits")
      _           <- ZIO.debug(s"result:\n${res.data}")
    } yield 0

  override def run: IO[ValidationError, Int] = test2
}
