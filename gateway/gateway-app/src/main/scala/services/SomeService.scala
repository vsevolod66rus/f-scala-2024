package ru.sskie.vpered
package services

import models.domain.SomeModel
import repos.SomeRepo
import zio._

trait SomeService {
  def simpleMethod(name: String): Task[SomeModel]

}

object SomeService {
  val live: URLayer[SomeRepo, SomeService] = ZLayer.fromFunction(SomeServiceImpl.apply _)
}

final case class SomeServiceImpl(repo: SomeRepo) extends SomeService {

  override def simpleMethod(name: String): Task[SomeModel] = repo.simpleMethod(name)

}
