package example

import cats.Parallel
import cats.effect.{Concurrent, ExitCode, IO, IOApp}
import cats.implicits.catsSyntaxOptionId
import fetch.{Data, DataCache, DataSource, Fetch, InMemoryCache}
import cats.implicits._

object FetchRunLogExample extends IOApp {

  private def fun1(id: Int) = IO.delay { println("hit1"); s"value1=$id".some }
  private def fun2(id: Int) = IO.delay { println("hit2"); s"value2=$id".some }
  private def fun3(id: Int) = IO.delay { println("hit3"); s"value3=$id".some }

  class DataSource1 extends Data[Int, String] {
    override def name: String               = "DataSource1"
    private def instance: DataSource1       = this
    def source: DataSource[IO, Int, String] = new DataSource[IO, Int, String] {
      override def data: Data[Int, String]            = instance
      override def CF: Concurrent[IO]                 = Concurrent[IO]
      override def fetch(id: Int): IO[Option[String]] = fun1(id)
    }
  }

  class DataSource2 extends Data[Int, String] {
    override def name: String               = "DataSource2"
    private def instance: DataSource2       = this
    def source: DataSource[IO, Int, String] = new DataSource[IO, Int, String] {
      override def data: Data[Int, String]            = instance
      override def CF: Concurrent[IO]                 = Concurrent[IO]
      override def fetch(id: Int): IO[Option[String]] = fun2(id)
    }
  }

  class DataSource3 extends Data[Int, String] {
    override def name: String               = "DataSource3"
    private def instance: DataSource3       = this
    def source: DataSource[IO, Int, String] = new DataSource[IO, Int, String] {
      override def data: Data[Int, String]            = instance
      override def CF: Concurrent[IO]                 = Concurrent[IO]
      override def fetch(id: Int): IO[Option[String]] = fun3(id)
    }
  }

  val dataSource1 = new DataSource1().source
  val dataSource2 = new DataSource2().source
  val dataSource3 = new DataSource3().source

  val query: Fetch[IO, List[String]] =
    for {
      value1 <- Fetch(1, dataSource1)
      value2 <- Fetch(2, dataSource2)
      value3 <- Fetch(3, dataSource3)

      value1again <- Fetch(1, dataSource1)
      value2again <- Fetch(2, dataSource2)
      value3again <- Fetch(3, dataSource3)
    } yield List(value1, value2, value3, value1again, value2again, value3again)

  val queryApplicative1: Fetch[IO, (String, String)]                    = (Fetch(1, dataSource1), Fetch(2, dataSource2)).tupled
  val queryApplicative2: Fetch[IO, (String, String)]                    = (Fetch(1, dataSource1), Fetch(3, dataSource3)).tupled
  val queryApplicative: Fetch[IO, ((String, String), (String, String))] = (queryApplicative1, queryApplicative2).tupled

  override def run(args: List[String]): IO[ExitCode] =
    for {
//      res             <- Fetch.run(query, cache = InMemoryCache.empty[IO])
      cacheWithResult <- Fetch.runCache(query)                   // (DataCache[F], A)
      _                = println(cacheWithResult._2)
      runLog          <- Fetch.runLog(query, cacheWithResult._1) // (Log, A)
      _                = runLog._1.rounds.foreach(println)

      _ <- Fetch.run(queryApplicative) // only 1 hit 1
    } yield ExitCode.Success
}
