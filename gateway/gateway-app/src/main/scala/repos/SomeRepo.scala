package ru.sskie.vpered
package repos

import models.domain.SomeModel
import zio._

trait SomeRepo {
  def simpleMethod(name: String): Task[SomeModel]

}

object SomeRepo {
  val live: ULayer[SomeRepo] = ZLayer.fromFunction(SomeRepoImpl.apply _)
}

final case class SomeRepoImpl() extends SomeRepo {

  override def simpleMethod(name: String): Task[SomeModel] = ZIO.succeed(SomeModel(field = s"kurwa $name"))

}
