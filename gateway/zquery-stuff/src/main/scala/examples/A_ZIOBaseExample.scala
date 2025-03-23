package ru.sskie.vpered.zquery
package examples

import zio._
import zio.query._

object A_ZIOBaseExample extends ZIOAppDefault {

  def getUserNameById(id: Int): Task[String] =
    ZIO
      .succeed(s"user name for id = $id")
      .debug("hit getUserNameById")

  def getUserNamesByIds(ids: List[Int]): Task[List[(Int, String)]] =
    ZIO
      .succeed(ids.map(id => (id, s"user name for id = $id")))
      .debug("hit getUserNamesByIds")

  case class GetUserName(id: Int) extends Request[Throwable, String]

  val UserDataSource: DataSource.Batched[Any, GetUserName] =
    new DataSource.Batched[Any, GetUserName] {
      val identifier: String = "UserDataSource"

      def run(requests: Chunk[GetUserName])(implicit trace: Trace): ZIO[Any, Nothing, CompletedRequestMap] =
        requests.toList match {
          case request :: Nil =>
            getUserNameById(request.id).exit
              .map(CompletedRequestMap.single(request, _))

          case batch: List[GetUserName] =>
            getUserNamesByIds(batch.map(_.id))
              .foldCause(
                CompletedRequestMap.failCause(requests, _),
                CompletedRequestMap.fromIterableWith(_)(kv => GetUserName(kv._1), kv => Exit.succeed(kv._2))
              )
        }
    }

  def getUserNameByIdQuery(id: Int): ZQuery[Any, Throwable, String] =
    ZQuery.fromRequest(GetUserName(id))(UserDataSource)

  val querySingle: ZQuery[Any, Throwable, List[String]] =
    for {
      ids   <- ZQuery.succeed(List(1))
      names <- ZQuery.foreachPar(ids)(id => getUserNameByIdQuery(id))
    } yield names

  val queryBatched: ZQuery[Any, Throwable, List[String]] =
    for {
      ids   <- ZQuery.succeed(List(1, 2, 3))
      names <- ZQuery.foreachPar(ids)(id => getUserNameByIdQuery(id))
    } yield names

  // withoutQuery:
  val effect = for {
    ids <- ZIO.succeed(List(1, 2, 3))
    res <- ids match {
             case id :: Nil      => getUserNameById(id)
             case ids: List[Int] => getUserNamesByIds(ids)
           }
  } yield res

  override def run: IO[Any, ExitCode] =
    for {
      _ <- ZIO.debug("querySingle: ")
      _ <- querySingle.run
      _ <- ZIO.debug("queryBatched: ")
      _ <- queryBatched.run
    } yield ExitCode.success

}
