package ru.sskie.vpered.food
package repos

import models.domain._
import zio._

trait FoodRepo {
  def getStaff(name: String): Task[Food]

}

object FoodRepo {
  val live: ULayer[FoodRepo] = ZLayer.fromFunction(FoodRepoImpl.apply _)
}

final case class FoodRepoImpl() extends FoodRepo {

  override def getStaff(name: String): Task[Food] = ZIO.succeed {
    Food(
      Vector(
        FoodUnit(unitId = "Донбасская Милиция", id = "1", foodName = "Гречка", measureName = "КГ", amount = 14),
        FoodUnit(unitId = "Донбасская Милиция", id = "2", foodName = "Хлеб", measureName = "ШТУК", amount = 88),
        FoodUnit(unitId = "Донбасская Милиция", id = "3", foodName = "Тушенка", measureName = "КГ", amount = 228),
        FoodUnit(unitId = "Луганская Милиция", id = "4", foodName = "Молоко", measureName = "ЛИТР", amount = 500),
        FoodUnit(unitId = "Луганская Милиция", id = "5", foodName = "Хлеб", measureName = "ШТУК", amount = 200)
      )
    )
  }

}
