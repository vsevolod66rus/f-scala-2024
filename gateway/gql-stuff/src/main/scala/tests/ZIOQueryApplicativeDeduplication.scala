package ru.sskie.vpered.gql
package tests

import zio._
import zio.query._
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

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

  class CustomCache(private val map: ConcurrentHashMap[Request[_, _], Promise[_, _]]) extends Cache {

    def get[E, A](request: Request[E, A])(implicit trace: Trace): IO[Unit, Promise[E, A]] =
      ZIO.suspendSucceed {
        val out = map.get(request).asInstanceOf[Promise[E, A]]
        val getF = if (out eq null) Exit.fail(()) else Exit.succeed(out)
        ZIO.log(s"CustomCache.get for $request") *> getF
      }

    def lookup[E, A, B](request: A)(implicit
                                    ev: A <:< Request[E, B],
                                    trace: Trace
    ): UIO[Either[Promise[E, B], Promise[E, B]]] =
      ZIO.succeed(lookupUnsafe[E, String, B](request)(Unsafe.unsafe(identity)))


    def lookupUnsafe[E, A, B](request: Request[_, _])(implicit
                                                      unsafe: Unsafe
    ): Either[Promise[E, B], Promise[E, B]] = {
      val newPromise = Promise.unsafe.make[E, B](FiberId.None)
//      val isContains1   = map.contains(request)
      val existing   = map.putIfAbsent(request, newPromise).asInstanceOf[Promise[E, B]]
//      if (isContains1) println(s"CustomCache.lookup-none for $request") else println(s"CustomCache.lookup-put for $request")
      if (existing eq null) Left(newPromise) else Right(existing)
    }

    def put[E, A](request: Request[E, A], result: Promise[E, A])(implicit trace: Trace): UIO[Unit] =
      ZIO.log(s"CustomCache.put for $request") *> ZIO.succeed(map.put(request, result))

    def remove[E, A](request: Request[E, A])(implicit trace: Trace): UIO[Unit] =
      ZIO.log(s"CustomCache.remove for $request") *> ZIO.succeed(map.remove(request))
  }

  override def run: IO[Any, ExitCode] =
    for {
      clock <- ZIO.clock
      t1    <- clock.currentTime(TimeUnit.MILLISECONDS)
//      res   <- query.run // foo hit 1, но как будто из кеша
      customCache = new CustomCache(new ConcurrentHashMap())
      res <- query.runCache(cache = customCache)
      t2    <- clock.currentTime(TimeUnit.MILLISECONDS)
      _     <- ZIO.debug(s"res=$res")
      _     <- ZIO.debug(t2 - t1)
    } yield ExitCode.success
}
