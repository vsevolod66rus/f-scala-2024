package ru.sskie.vpered

import models.configs.AppConfig
import repos.{DBTransactor, TestRepo}
import zio.{ExitCode, IO, ZIOAppDefault}
import zio._

object ZQueryApp extends ZIOAppDefault {

  private val mainApp = for {
    repo <- ZIO.service[TestRepo]
//    _    <- repo.withoutZQuery
//    _    <- repo.withZQuery
    _    <- repo.withoutZQueryOne2M.timed.map { case (time, _) =>
              ZIO.logInfo(s"withoutZQuery millis time: ${time.toMillis}")
            }
    _    <- repo.withZQueryOne2M.timed.map { case (time, _) =>
              ZIO.logInfo(s"withZQuery millis time: ${time.toMillis}")
            }
  } yield ExitCode.success

  override def run: IO[Any, ExitCode] = mainApp
    .provide(
      // Config
      AppConfig.live,
      AppConfig.allConfigs,
      // Repo
      DBTransactor.appXaLive,
      TestRepo.live
    )
}
