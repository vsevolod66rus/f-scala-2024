package ru.sskie.vpered
package cache

import zio.{IO, Promise, Trace, UIO}
import zio.query.{Cache, Request}

class CustomCacheImpl extends zio.query.Cache {
  override def get[E, A](request: Request[E, A])(implicit trace: Trace): IO[Unit, Promise[E, A]] = ???

  override def lookup[E, A, B](
      request: A
  )(implicit ev: A <:< Request[E, B], trace: Trace): UIO[Either[Promise[E, B], Promise[E, B]]] = ???

  override def put[E, A](request: Request[E, A], result: Promise[E, A])(implicit trace: Trace): UIO[Unit] = ???

  override def remove[E, A](request: Request[E, A])(implicit trace: Trace): UIO[Unit] = ???
}
