package ru.sskie.vpered.zquery
package examples

import zio.{ExitCode, FiberRef, IO, Unsafe, ZIO, ZIOAppDefault}
import zio.query.{Cache, DataSource, Request, ZQuery}

object ZIOQueryRunLog extends ZIOAppDefault {

  private def fun1(id: String) = ZIO.succeed(s"value1=$id").tap(_ => ZIO.debug("hit1"))
  private def fun2(id: String) = ZIO.succeed(s"value2=$id").tap(_ => ZIO.debug("hit2"))
  private def fun3(id: String) = ZIO.succeed(s"value3=$id").tap(_ => ZIO.debug("hit3"))

  case class DataSource1(id: String) extends Request[Throwable, String]
  val datasource1: DataSource[Any, DataSource1] =
    DataSource.fromFunctionZIO("DataSource1")(ds1 => fun1(ds1.id))

  case class DataSource2(id: String) extends Request[Throwable, String]
  val datasource2: DataSource[Any, DataSource2] =
    DataSource.fromFunctionZIO("DataSource2")(ds2 => fun2(ds2.id))

  case class DataSource3(id: String) extends Request[Throwable, String]
  val datasource3: DataSource[Any, DataSource3] =
    DataSource.fromFunctionZIO("DataSource3")(ds3 => fun3(ds3.id))

  private val zQuery: ZQuery[Any, Throwable, List[String]] = for {
    value1 <- ZQuery.fromRequest(DataSource1(id = "1"))(datasource1)
    value2 <- ZQuery.fromRequest(DataSource2(id = "2"))(datasource2)
    value3 <- ZQuery.fromRequest(DataSource3(id = "3"))(datasource3)

    value1again <- ZQuery.fromRequest(DataSource1(id = "1"))(datasource1)
    value2again <- ZQuery.fromRequest(DataSource2(id = "2"))(datasource2)
    value3again <- ZQuery.fromRequest(DataSource3(id = "3"))(datasource3)
  } yield List(value1, value2, value3, value1again, value2again, value3again)

  override def run: IO[Any, ExitCode] =
    for {
      myCache <- Cache.empty
      res     <- zQuery.runCache(cache = myCache)
      _       <- ZIO.debug(res)
      runLog  <- zQuery.runLog
      _       <- ZIO.debug(runLog)
    } yield ExitCode.success
}
