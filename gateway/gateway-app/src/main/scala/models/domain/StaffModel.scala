package ru.sskie.vpered
package models.domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import zio.schema.DeriveSchema.gen

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
  implicit lazy val bCodec              = zio.schema.codec.JsonCodec.schemaBasedBinaryCodec[Staff]
}

object StaffUnit {
  implicit lazy val codec: Codec[StaffUnit] = deriveCodec
}
