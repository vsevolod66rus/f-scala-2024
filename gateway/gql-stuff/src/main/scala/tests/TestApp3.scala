package ru.sskie.vpered.gql
package tests

import caliban.CalibanError.ValidationError
import caliban._
import caliban.schema.Schema.auto._
import caliban.schema.ArgBuilder.auto._
import zio.{IO, ZIO, ZIOAppDefault}

object TestApp3 extends ZIOAppDefault {

  val test3: IO[ValidationError, Int] =
    for {
      dbService   <- DBService()
      resolver     = Api3.resolver(dbService)
      api          = graphQL(RootResolver(resolver))
      interpreter <- api.interpreter
      res         <- interpreter.execute(Query.orders)
      dbHits      <- dbService.hits
      _           <- ZIO.debug(s"ZQuery - DB Hits: $dbHits")
      _           <- ZIO.debug(s"result:\n${res.data}")
    } yield 0

  override def run: IO[ValidationError, Int] = test3
}
