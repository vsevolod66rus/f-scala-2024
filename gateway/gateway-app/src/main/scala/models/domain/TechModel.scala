package ru.sskie.vpered
package models.domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec
import zio.schema.DeriveSchema.gen

case class Tech(units: Vector[TechUnit])

case class TechUnit(
    unitId: String,
    id: String,
    techType: String,
    techName: String,
    ownerId: String
)

object Tech {
  implicit lazy val codec: Codec[Tech] = deriveCodec
  implicit lazy val bCodec             = zio.schema.codec.JsonCodec.schemaBasedBinaryCodec[Tech]
}

object TechUnit {
  implicit lazy val codec: Codec[TechUnit] = deriveCodec
}
