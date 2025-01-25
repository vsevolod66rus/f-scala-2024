package ru.sskie.vpered.gql
package example

import caliban.CalibanError.ValidationError
import caliban._
import caliban.schema.ArgBuilder.auto._
import caliban.schema.Schema.auto._
import caliban.RootResolver
import zio.{IO, ZIO, ZIOAppDefault}

object TestApp1 extends ZIOAppDefault {

  val test1: IO[ValidationError, Int] =
    for {
      dbService   <- DBService()
      resolver     = Api1.resolver(dbService)
      api          = graphQL(RootResolver(resolver))
      interpreter <- api.interpreter
      _           <- interpreter.execute(Query.orders)
      dbHits      <- dbService.hits
      _           <- ZIO.debug(s"Naive Approach - DB Hits: $dbHits")
    } yield 0

  override def run: IO[ValidationError, Int] = test1
}
