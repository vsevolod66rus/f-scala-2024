package ru.sskie.vpered
package repos

import cats.data.NonEmptyList
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import zio._
import zio.interop.catz._
import zio.query._

trait TestRepo {
  def withoutZQuery: Task[Unit]

  def withZQuery: Task[Unit]

  def withoutZQueryOne2M: Task[Unit]

  def withZQueryOne2M: Task[Unit]

  def withZQueryOne2MRunLog: Task[Unit]

  def withZQueryFromRequests: Task[Unit]
}

object TestRepo {
  val live: URLayer[DBTransactor, TestRepo] = ZLayer.fromFunction(TestRepoImpl.apply _)
}

final case class TestRepoImpl(transactor: DBTransactor) extends TestRepo {

  override def withoutZQuery: Task[Unit] = for {
    ids   <- getAllUserIds
    names <- ZIO.foreachPar(ids)(getUserNameById)
    _     <- ZIO.logInfo(s"withoutZQuery selected ${names.size}")
  } yield ()

  override def withZQuery: Task[Unit] = query.run.flatMap(names => ZIO.logInfo(s"withZQuery selected ${names.size}"))

  override def withoutZQueryOne2M: Task[Unit] = for {
    ids   <- getAllUserDepartmentsIds
    names <- ZIO.foreachPar(ids)(getDepartmentNameById)
    _     <- ZIO.logInfo(s"withoutZQuery selected departments ${names.size}")
  } yield ()

  override def withZQueryOne2M: Task[Unit] =
    for {
      names <- queryDepartments.run
      _     <- ZIO.logInfo(s"withZQuery selected departments ${names.size}")
    } yield ()

  override def withZQueryFromRequests: Task[Unit] =
    for {
      names <- queryUsersWithCustomBatch.run
      _     <- ZIO.logInfo(s"withZQuery selected departments ${names.size}")
    } yield ()

  // и что?)
  override def withZQueryOne2MRunLog: Task[Unit] =
    for {
      res     <- queryDepartments.runLog
      _       <- ZIO.logInfo(s"withZQuery selected departments ${res.size}")
      cache    = res._1
      lookup1 <- cache.lookup(GetDepartmentName("2e707ab4-4e7e-447c-9f7e-d4f919f2da67"))
      _        = println(s"lookup1 2e707ab4-4e7e-447c-9f7e-d4f919f2da67 = $lookup1")
      _       <- cache.remove(GetDepartmentName("2e707ab4-4e7e-447c-9f7e-d4f919f2da67"))
      lookup2 <- cache.lookup(GetDepartmentName("2e707ab4-4e7e-447c-9f7e-d4f919f2da67"))
      _        = println(s"lookup2 2e707ab4-4e7e-447c-9f7e-d4f919f2da67 = $lookup2")
    } yield ()

  private val query: ZQuery[Any, Throwable, List[String]] = for {
    ids   <- ZQuery.fromZIO(getAllUserIds)
    names <- ZQuery.foreachPar(ids)(id => ZQuery.fromRequest(GetUserName(id))(userDataSource)).map(_.toList)
  } yield names

  private def getAllUserIds: Task[List[String]] = {
    val sql = sql"select id from hr.employees"
    sql.query[String].to[List].transact(transactor.xa)
  }

  private def getUserNameById(id: String): Task[String] = {
    val sql = sql"select full_name from hr.employees where id::text = $id"
    sql.query[String].unique.transact(transactor.xa)
  }

  private def getUserNameByIds(ids: NonEmptyList[String]): Task[List[(String, String)]] = {
    println(s"DB METHOD getUserNameByIds with size: ${ids.size}")
    val sql = sql"select id, full_name from hr.employees where ${Fragments.in(fr"id::text", ids)}"
    sql.query[(String, String)].to[List].transact(transactor.xa)
  }

  case class GetUserName(id: String) extends Request[Throwable, String]

  lazy val userDataSource: DataSource.Batched[Any, GetUserName] =
    new DataSource.Batched[Any, GetUserName] {
      val identifier: String = "UserDataSource"

      def run(requests: Chunk[GetUserName])(implicit trace: Trace): ZIO[Any, Nothing, CompletedRequestMap] =
        requests.toList match {
          case request :: Nil =>
            val result: Task[String] =
              // get user by ID e.g. SELECT name FROM users WHERE id = $id
              getUserNameById(request.id)

            result.exit.map(CompletedRequestMap.single(request, _))

          case batch: Seq[GetUserName] =>
            val result: Task[List[(String, String)]] =
              // get multiple users at once e.g. SELECT id, name FROM users WHERE id IN ($ids)
              NonEmptyList
                .fromList(batch.map(_.id))
                .fold(List.empty[(String, String)].pure[Task])(getUserNameByIds)

            result.foldCause(
              CompletedRequestMap.failCause(requests, _),
              CompletedRequestMap.fromIterableWith(_)(kv => GetUserName(kv._1), kv => Exit.succeed(kv._2))
            )
        }

    }

//  departments one department to many users
  private def getAllUserDepartmentsIds: Task[List[String]] = {
    val sql = sql"select department_id from hr.employees"
    sql.query[String].to[List].transact(transactor.xa)
  }

  private def getDepartmentNameById(id: String): Task[String] = {
    println(s"getDepartmentNameById: $id")
    val sql = sql"select name from hr.departments where id::text = $id"
    sql.query[String].unique.transact(transactor.xa)
  }

  case class GetDepartmentName(id: String) extends Request[Throwable, String]
  val datasource: DataSource[Any, GetDepartmentName] =
    DataSource.fromFunctionZIO("GetDepartmentName")(request => getDepartmentNameById(request.id))

  private val queryDepartments: ZQuery[Any, Throwable, List[String]] = for {
    ids   <- ZQuery.fromZIO(getAllUserDepartmentsIds)
    names <- ZQuery.foreachPar(ids)(id => ZQuery.fromRequest(GetDepartmentName(id))(datasource)).map(_.toList)
  } yield names

  private val queryUsersWithCustomBatch: ZQuery[Any, Throwable, List[String]] = for {
    ids       <- ZQuery.fromZIO(getAllUserIds).map(_.take(10))
    first5     = ids.take(5)
    _         <- ZQuery.succeed(println(s"ZQUERY requested ZQuery with size: ${first5.size}"))
    first5Res <- ZQuery.fromRequests(first5.map(id => GetUserName(id)))(userDataSource)
    _         <- ZQuery.succeed(println(s"ZQUERY requested ZQuery with size: ${ids.size}"))
    all       <- ZQuery.fromRequests(ids.map(id => GetUserName(id)))(userDataSource)
  } yield all

}
