package ru.sskie.vpered.tech
package models.domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

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
}

object TechUnit {
  implicit lazy val codec: Codec[TechUnit] = deriveCodec
}
