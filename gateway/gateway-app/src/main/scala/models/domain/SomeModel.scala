package ru.sskie.vpered.models.domain

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class SomeModel(field: String)

object SomeModel {
  implicit lazy val codec: Codec[SomeModel] = deriveCodec
}
