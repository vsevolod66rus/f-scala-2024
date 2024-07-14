package ru.sskie.vpered

import controllers.SomeController
import models.configs.{AppConfig, ServerConfig}
import repos.SomeRepo
import services.SomeService
import zio.http.Server
import zio.logging.backend.SLF4J
import zio.{ExitCode, IO, Runtime, ZIO, ZIOAppDefault, ZLayer}

object App extends ZIOAppDefault {

  private val loggerLayer = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  private val httpApp = for {
    exampleApi <- ZIO.service[SomeController]
    _          <- Server.serve(exampleApi.routes.withDefaultErrorResponse)
    exitCode   <- ZIO.never.exitCode
  } yield exitCode

  private val zioSeverConfig = ZLayer.fromFunction { config: ServerConfig =>
    Server.Config.default.port(config.port)
  }

  private val mainApp = AppConfig.logConfig.flatMap { conf =>
    ZIO.logInfo(s"See http://${Server.Config.default.address.getHostName}:${conf.server.port}/docs")
  } *> httpApp

  override def run: IO[Any, ExitCode] = mainApp
    .provide(
      // Config
      AppConfig.live,
      AppConfig.allConfigs,
      // Repo
      SomeRepo.live,
      // Service
      SomeService.live,
      // Controller
      SomeController.live,
      // Server
      zioSeverConfig,
      Server.live,
      loggerLayer
    )
}
