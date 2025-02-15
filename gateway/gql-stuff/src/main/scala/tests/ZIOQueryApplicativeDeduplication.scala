package ru.sskie.vpered.gql
package tests

import zio.query.{DataSource, Request, ZQuery}
import zio.{ExitCode, IO, ZIO, ZIOAppDefault, durationInt}

import java.util.concurrent.TimeUnit

object ZIOQueryApplicativeDeduplication extends ZIOAppDefault {

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

  def queryFooBar = getFooByIdQuery(1).zipPar(getBarByIdQuery(2))
  def queryFooBaz = getFooByIdQuery(1).zipPar(getBazByIdQuery(3))
  def query       = queryFooBar.zipPar(queryFooBaz)

  override def run: IO[Any, ExitCode] =
    for {
      clock <- ZIO.clock
      t1    <- clock.currentTime(TimeUnit.MILLISECONDS)
      res   <- query.run // foo hit 1, но как будто из кеша
//      res <- query.runCache(cache = ) // TODO
      t2    <- clock.currentTime(TimeUnit.MILLISECONDS)
      _     <- ZIO.debug(s"res=$res")
      _     <- ZIO.debug(t2 - t1)
    } yield ExitCode.success
}
