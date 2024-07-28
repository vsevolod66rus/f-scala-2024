package ru.sskie.vpered.staff
package services

import models.domain.Staff
import repos.StaffRepo
import zio._

trait StaffService {
  def getStaff(name: String): Task[Staff]

}

object StaffService {
  val live: URLayer[StaffRepo, StaffService] = ZLayer.fromFunction(StaffServiceImpl.apply _)
}

final case class StaffServiceImpl(repo: StaffRepo) extends StaffService {

  override def getStaff(name: String): Task[Staff] = repo.getStaff(name)

}
