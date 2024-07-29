package ru.sskie.vpered
package models.domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class AggregateUnitInfoDTO(staff: Vector[StaffUnit], tech: Vector[TechUnit], food: Vector[FoodUnit])

object AggregateUnitInfoDTO {
  implicit lazy val codec: Codec[AggregateUnitInfoDTO] = deriveCodec
}
