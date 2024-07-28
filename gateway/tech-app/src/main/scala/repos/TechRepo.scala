package ru.sskie.vpered.tech
package repos

import models.domain._
import zio._

trait TechRepo {
  def getStaff(name: String): Task[Tech]

}

object TechRepo {
  val live: ULayer[TechRepo] = ZLayer.fromFunction(TechRepoImpl.apply _)
}

final case class TechRepoImpl() extends TechRepo {

  override def getStaff(name: String): Task[Tech] = ZIO.succeed {
    Tech(
      Vector(
        TechUnit(unitId = "Донбасская Милиция", id = "1", techType = "ПВО", techName = "ЗРК БУК", ownerId = "1"),
        TechUnit(unitId = "Донбасская Милиция", id = "2", techType = "ПВО", techName = "ЗРК БУК", ownerId = "1"),
        TechUnit(unitId = "Донбасская Милиция", id = "3", techType = "ПВО", techName = "ЗРК БУК", ownerId = "1"),
        TechUnit(unitId = "Луганская Милиция", id = "4", techType = "БТТ", techName = "ТАНК Т-34", ownerId = "5"),
        TechUnit(unitId = "Луганская Милиция", id = "5", techType = "БТТ", techName = "ТАНК АБРАМС", ownerId = "5")
      )
    )
  }

}
