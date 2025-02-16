package ru.sskie.vpered.gql
package tests

import zio.query.{DataSource, Request, ZQuery}
import zio.{ExitCode, IO, ZIO, ZIOAppDefault, durationInt}

import java.util.concurrent.TimeUnit

object ZIOQueryApplicativeDeduplicationFromZIO extends ZIOAppDefault {

  def getFooById(id: Int) = for {
    _ <- ZIO.debug(s"hit foo id=$id")
    _ <- ZIO.sleep(1.second)
  } yield s"foo id=$id"

  def getBarById(id: Int) = for {
    _ <- ZIO.debug(s"hit bar id=$id")
    _ <- ZIO.sleep(1.second)
  } yield s"bar id=$id"

  def getBazById(id: Int) = for {
    _ <- ZIO.debug(s"hit baz id=$id")
    _ <- ZIO.sleep(1.second)
  } yield s"baz id=$id"

  def queryFooBar = ZQuery.fromZIO(getFooById(1)).zipPar(ZQuery.fromZIO(getBarById(2)))
  def queryFooBaz = ZQuery.fromZIO(getFooById(1)).zipPar(ZQuery.fromZIO(getBazById(3)))
  def query       = queryFooBar.zipPar(queryFooBaz)

  override def run: IO[Any, ExitCode] =
    for {
      clock <- ZIO.clock
      t1    <- clock.currentTime(TimeUnit.MILLISECONDS)
      res   <- query.run
      t2    <- clock.currentTime(TimeUnit.MILLISECONDS)
      _     <- ZIO.debug(s"res=$res")
      _     <- ZIO.debug(t2 - t1)
    } yield ExitCode.success
}
