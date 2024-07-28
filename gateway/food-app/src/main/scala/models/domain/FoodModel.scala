package ru.sskie.vpered.food
package models.domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Food(units: Vector[FoodUnit])

case class FoodUnit(
    unitId: String,
    id: String,
    foodName: String,
    measureName: String,
    amount: Double
)

object Food {
  implicit lazy val codec: Codec[Food] = deriveCodec
}

object FoodUnit {
  implicit lazy val codec: Codec[FoodUnit] = deriveCodec
}
