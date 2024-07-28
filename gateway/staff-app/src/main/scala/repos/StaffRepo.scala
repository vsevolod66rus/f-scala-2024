package ru.sskie.vpered.staff
package repos

import models.domain._
import zio._

trait StaffRepo {
  def getStaff(name: String): Task[Staff]

}

object StaffRepo {
  val live: ULayer[StaffRepo] = ZLayer.fromFunction(StaffRepoImpl.apply _)
}

final case class StaffRepoImpl() extends StaffRepo {

  override def getStaff(name: String): Task[Staff] = ZIO.succeed {
    Staff(
      Vector(
        StaffUnit(unitId = "Донбасская Милиция", id = "1", fullName = "Первый", age = 14, rank = "Ополченец"),
        StaffUnit(unitId = "Донбасская Милиция", id = "2", fullName = "Второй", age = 14, rank = "Ополченец"),
        StaffUnit(unitId = "Донбасская Милиция", id = "3", fullName = "Третий", age = 14, rank = "Ополченец"),
        StaffUnit(unitId = "Луганская Милиция", id = "4", fullName = "Четвертый", age = 88, rank = "Ополченец"),
        StaffUnit(unitId = "Луганская Милиция", id = "5", fullName = "Пятый", age = 88, rank = "Старшина")
      )
    )
  }

}
