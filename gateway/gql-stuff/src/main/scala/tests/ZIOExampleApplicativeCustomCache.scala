package ru.sskie.vpered.gql
package tests

import zio.{ExitCode, IO, ZIO, ZIOAppDefault, durationInt}

import java.util.concurrent.TimeUnit

object ZIOExampleApplicativeCustomCache extends ZIOAppDefault {

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

  var simpleFooCache = Map.empty[Int, String]
  var simpleBarCache = Map.empty[Int, String]
  var simpleBazCache = Map.empty[Int, String]

  def getFooCached(id: Int) =
    simpleFooCache
      .get(id)
      .fold(getFooById(id).tap(res => ZIO.succeed { simpleFooCache = simpleFooCache.updated(id, res) }))(
        ZIO.succeed(_).debug(s"from cache foo id=$id")
      )
  def getBarCached(id: Int) =
    simpleBarCache.get(id).fold(getBarById(id))(ZIO.succeed(_).debug(s"from cache foo id=$id"))
  def getBazCached(id: Int) =
    simpleBazCache.get(id).fold(getBazById(id))(ZIO.succeed(_).debug(s"from cache foo id=$id"))

  def fooBarApplicative = getFooCached(1).zipPar(getBarCached(1))
  def fooBazApplicative = getFooCached(1).zipPar(getBazCached(1))
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
      t2    <- clock.currentTime(TimeUnit.MILLISECONDS)
      _     <- ZIO.debug(s"res = $res")
      _     <- ZIO.debug(s"execution time = ${t2 - t1} millis")
    } yield ExitCode.success
}
