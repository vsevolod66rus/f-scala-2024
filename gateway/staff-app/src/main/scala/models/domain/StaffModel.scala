package ru.sskie.vpered.staff
package models.domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class Staff(units: Vector[StaffUnit])

case class StaffUnit(
    unitId: String,
    id: String,
    fullName: String,
    age: Int,
    rank: String
)

object Staff {
  implicit lazy val codec: Codec[Staff] = deriveCodec
}

object StaffUnit {
  implicit lazy val codec: Codec[StaffUnit] = deriveCodec
}
