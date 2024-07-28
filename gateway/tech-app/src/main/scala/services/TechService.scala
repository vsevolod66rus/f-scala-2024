package ru.sskie.vpered.tech
package services

import models.domain.Tech
import repos.TechRepo
import zio._

trait TechService {
  def getStaff(name: String): Task[Tech]

}

object TechService {
  val live: URLayer[TechRepo, TechService] = ZLayer.fromFunction(TechServiceImpl.apply _)
}

final case class TechServiceImpl(repo: TechRepo) extends TechService {

  override def getStaff(name: String): Task[Tech] = repo.getStaff(name)

}
