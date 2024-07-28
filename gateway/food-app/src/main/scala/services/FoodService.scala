package ru.sskie.vpered.food
package services

import models.domain.Food
import repos.FoodRepo
import zio._

trait FoodService {
  def getStaff(name: String): Task[Food]

}

object FoodService {
  val live: URLayer[FoodRepo, FoodService] = ZLayer.fromFunction(FoodServiceImpl.apply _)
}

final case class FoodServiceImpl(repo: FoodRepo) extends FoodService {

  override def getStaff(name: String): Task[Food] = repo.getStaff(name)

}
