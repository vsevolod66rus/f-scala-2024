package ru.sskie.vpered.gql
package tests

import zio._
import zio.query._

import java.util.concurrent.TimeUnit

object C_ZIOQueryApplicativeDeduplication extends ZIOAppDefault {

  def getFooById(id: Int) = for {
    _ <- ZIO.debug(s"hit foo id = $id")
    _ <- ZIO.sleep(1.second)
  } yield s"foo id = $id"
  def getBarById(id: Int) = for {
    _ <- ZIO.debug(s"hit bar id = $id")
    _ <- ZIO.sleep(1.second)
  } yield s"bar id = $id"
  def getBazById(id: Int) = for {
    _ <- ZIO.debug(s"hit baz id = $id")
    _ <- ZIO.sleep(1.second)
  } yield s"baz id = $id"

  case class GetFooById(id: Int) extends Request[Nothing, String]
  val GetFooByIdDataSource: DataSource[Any, GetFooById]      =
    DataSource.fromFunctionZIO("GetFooById")(req => getFooById(req.id))
  def getFooByIdQuery(id: Int): ZQuery[Any, Nothing, String] = ZQuery.fromRequest(GetFooById(id))(GetFooByIdDataSource)

  case class GetBarById(id: Int) extends Request[Nothing, String]
  val GetBarByIdDataSource: DataSource[Any, GetBarById]      =
    DataSource.fromFunctionZIO("GetBarById")(req => getBarById(req.id))
  def getBarByIdQuery(id: Int): ZQuery[Any, Nothing, String] = ZQuery.fromRequest(GetBarById(id))(GetBarByIdDataSource)

  case class GetBazById(id: Int) extends Request[Nothing, String]
  val GetBazByIdDataSource: DataSource[Any, GetBazById]      =
    DataSource.fromFunctionZIO("GetBazById")(req => getBazById(req.id))
  def getBazByIdQuery(id: Int): ZQuery[Any, Nothing, String] = ZQuery.fromRequest(GetBazById(id))(GetBazByIdDataSource)

  def queryFooBar      = getFooByIdQuery(1).zipPar(getBarByIdQuery(1))
  def queryFooBaz      = getFooByIdQuery(1).zipPar(getBazByIdQuery(1))
  def queryApplicative = queryFooBar.zipPar(queryFooBaz)

  override def run: IO[Any, ExitCode] =
    for {
      clock <- ZIO.clock
      t1    <- clock.currentTime(TimeUnit.MILLISECONDS)
      res   <- queryApplicative.run
      t2    <- clock.currentTime(TimeUnit.MILLISECONDS)
      _     <- ZIO.debug(s"res = $res")
      _     <- ZIO.debug(s"execution time = ${t2 - t1} millis")
    } yield ExitCode.success
}
