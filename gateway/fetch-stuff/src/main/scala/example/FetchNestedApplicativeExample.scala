package ru.sskie.vpered.fetch
package example

import cats.effect.{Clock, Concurrent, ExitCode, IO, IOApp}
import cats.implicits._
import fetch.{Data, DataSource, Fetch}

import scala.concurrent.duration._

object FetchNestedApplicativeExample extends IOApp {

  private def fun1(id: Int) = IO.sleep(1000.millis).map { _ => println("hit1"); s"value1=$id".some }
  private def fun2(id: Int) = IO.sleep(1000.millis).map { _ => println("hit2"); s"value2=$id".some }
  private def fun3(id: Int) = IO.sleep(1000.millis).map { _ => println("hit3"); s"value3=$id".some }

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

  val queryApplicative1: Fetch[IO, (String, String)]                    = (Fetch(1, dataSource1), Fetch(2, dataSource2)).tupled
  val queryApplicative2: Fetch[IO, (String, String)]                    = (Fetch(1, dataSource1), Fetch(3, dataSource3)).tupled
  val queryApplicative: Fetch[IO, ((String, String), (String, String))] = (queryApplicative1, queryApplicative2).tupled

  val queryMonad = for {
    res1 <- Fetch(1, dataSource1)
    res2 <- Fetch(1, dataSource1)
  } yield List(res1, res2)

  override def run(args: List[String]): IO[ExitCode] =
    for {
      t1           <- Clock.apply[IO].monotonic
      (cache, res) <- Fetch.runCache(queryApplicative) // only 1 hit 1, but takes from cache
      _             = println(res)
      t2           <- Clock.apply[IO].monotonic
      _             = println(s"t=${(t2 - t1).toMillis} millis")
    } yield ExitCode.Success

}
