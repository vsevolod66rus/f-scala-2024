package ru.sskie.vpered.gql
package tests

import zio._
import zio.query._

object ZIOBaseExample extends ZIOAppDefault {

  def getUserIds: Task[List[Int]]                                  =
    ZIO.succeed(List(1, 2, 3))
  def getUserNameById(id: Int): Task[String]                       =
    ZIO.succeed(s"user name for id=$id")
  def getUserNamesByIds(ids: List[Int]): Task[List[(Int, String)]] =
    ZIO.succeed(ids.map(id => (id, s"user name for id=$id")))

  case class GetUserName(id: Int) extends Request[Throwable, String]

  lazy val UserDataSource: DataSource.Batched[Any, GetUserName] =
    new DataSource.Batched[Any, GetUserName] {
      val identifier: String = "UserDataSource"

      def run(requests: Chunk[GetUserName])(implicit trace: Trace): ZIO[Any, Nothing, CompletedRequestMap] =
        requests.toList match {
          case request :: Nil =>
            val result: Task[String] = getUserNameById(request.id)
            result.exit.map(CompletedRequestMap.single(request, _))

          case batch: List[GetUserName] =>
            val result: Task[List[(Int, String)]] = getUserNamesByIds(batch.map(_.id))
            result.foldCause(
              CompletedRequestMap.failCause(requests, _),
              CompletedRequestMap.fromIterableWith(_)(kv => GetUserName(kv._1), kv => Exit.succeed(kv._2))
            )
        }
    }

  def getUserNameByIdQuery(id: Int): ZQuery[Any, Throwable, String] =
    ZQuery.fromRequest(GetUserName(id))(UserDataSource)

  val query: ZQuery[Any, Throwable, List[String]] =
    for {
      ids   <- ZQuery.fromZIO(getUserIds)
      names <- ZQuery.foreachPar(ids)(id => getUserNameByIdQuery(id)) /* будет параллельно без изменения семантики */
    } yield names

  // withoutQuery:
  val effect = for {
    ids <- getUserIds
    res <- ids match {
             case id :: Nil      => getUserNameById(id)
             case ids: List[Int] => getUserNamesByIds(ids)
           }
  } yield res

  override def run: IO[Any, ExitCode] =
    for {
      //      res <- effect
      res <- query.run
      _   <- ZIO.debug(s"res=$res")
    } yield ExitCode.success
}
