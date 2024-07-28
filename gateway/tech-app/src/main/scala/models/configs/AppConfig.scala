package ru.sskie.vpered.tech
package models.configs

import monocle.macros.GenLens
import zio._
import zio.config._
import zio.config.magnolia.descriptor
import zio.config.typesafe._

final case class AppConfig(server: ServerConfig, postgres: DBConfig)

object AppConfig {

  private type AllConfigs = ServerConfig with DBConfig

  val live: ULayer[AppConfig] = TypesafeConfig.fromResourcePath(descriptor[AppConfig]).orDie

  val allConfigs: URLayer[AppConfig, AllConfigs] = subConf(_.server) >+> subConf(_.postgres)

  private def hidePasswords(config: AppConfig): AppConfig = {
    val setters = Seq(GenLens[AppConfig](_.postgres.password))
    setters.foldLeft(config)((conf, setter) => setter.replace("*****")(conf))
  }

  val logConfig: URIO[AppConfig, AppConfig] =
    for {
      appConfig <- ZIO.service[AppConfig].map(hidePasswords)
      config    <- ZIO
                     .fromEither(write(descriptor[AppConfig], appConfig))
                     .orDieWith(msg => new RuntimeException(s"Can't write config: $msg"))
      _         <- ZIO.logInfo(s"Service1 config:\n${config.toHoconString}")
    } yield appConfig

  private def subConf[T: Tag](accessor: AppConfig => T): URLayer[AppConfig, T] =
    ZLayer.fromFunction(accessor)

}
