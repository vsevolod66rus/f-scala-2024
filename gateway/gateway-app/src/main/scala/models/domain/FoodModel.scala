package ru.sskie.vpered
package models.domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import zio.schema.DeriveSchema.gen

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
  implicit lazy val bCodec             = zio.schema.codec.JsonCodec.schemaBasedBinaryCodec[Food]
}

object FoodUnit {
  implicit lazy val codec: Codec[FoodUnit] = deriveCodec
}
