package ru.sskie.vpered.gql
package tests

import zio._
import zio.query._

object D_ZIOBatchedCacheExample extends ZIOAppDefault {

  def getUserNamesByIds(ids: List[Int]): Task[List[(Int, String)]] =
    ZIO
      .debug(s"hit getUserNamesByIds with id = $ids")
      .map(_ => ids.map(id => (id, s"user name for id = $id")))

  case class GetUserName(id: Int) extends Request[Throwable, String]

  val UserDataSourceBatched: DataSource[Any, GetUserName] =
    DataSource.fromFunctionBatchedZIO("UserDataSourceBatched") { requests =>
      getUserNamesByIds(requests.map(_.id).toList).map(res => Chunk.fromIterable(res.map(_._2)))
    }

  def getUserNameByIdQuery(id: Int): ZQuery[Any, Throwable, String] =
    ZQuery.fromRequest(GetUserName(id))(UserDataSourceBatched)

  val query1to5: ZQuery[Any, Throwable, List[String]] =
    for {
      ids   <- ZQuery.succeed(List(1, 2, 3, 4, 5))
      names <- ZQuery.foreachPar(ids)(id => getUserNameByIdQuery(id))
    } yield names

  val query3to8: ZQuery[Any, Throwable, List[String]] =
    for {
      ids   <- ZQuery.succeed(List(3, 4, 5, 6, 7))
      names <- ZQuery.foreachPar(ids)(id => getUserNameByIdQuery(id))
    } yield names

  val query = query1to5.zip(query3to8)

  override def run: IO[Any, ExitCode] =
    for {
      (res1to5, res3to8) <- query.run
      _                   = println(s"\nres1to5:")
      _                   = res1to5.foreach(println)
      _                   = println(s"\nres3to8:")
      _                   = res3to8.foreach(println)
    } yield ExitCode.success
}
