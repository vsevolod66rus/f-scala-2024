package ru.sskie.vpered.zquery
package examples

import zio.{ExitCode, IO, ZIO, ZIOAppDefault, durationInt}

import java.util.concurrent.TimeUnit

object B_ZIOExampleApplicativeCustomCache extends ZIOAppDefault {

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

  def fooBarApplicative = getFooById(1).zipPar(getBarById(1))
  def fooBazApplicative = getFooById(1).zipPar(getBazById(1))
  def effectApplicative = fooBarApplicative.zipPar(fooBazApplicative)

  def effectMonad = for {
    res1 <- fooBarApplicative
    res2 <- fooBazApplicative
  } yield s"$res1, $res2"

  override def run: IO[Any, ExitCode] =
    for {
      clock <- ZIO.clock
      t1    <- clock.currentTime(TimeUnit.MILLISECONDS)
      res   <- effectApplicative

//      res   <- effectMonad //proof cache works

      t2 <- clock.currentTime(TimeUnit.MILLISECONDS)
      _  <- ZIO.debug(s"res = $res")
      _  <- ZIO.debug(s"execution time = ${t2 - t1} millis")
    } yield ExitCode.success
}
