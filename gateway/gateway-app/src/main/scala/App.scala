package ru.sskie.vpered

import controllers.SomeController
import models.configs.{AppConfig, ServerConfig}
import repos.SomeRepo
import services.SomeService

import zio.http.Server
import zio.logging.backend.SLF4J
import zio.metrics.connectors.prometheus.{PrometheusPublisher, prometheusLayer, publisherLayer}
import zio.{ExitCode, IO, Runtime, ZIO, ZIOAppDefault, ZLayer, durationInt}
import zio.metrics.connectors.MetricsConfig
import zio.metrics.jvm.DefaultJvmMetrics
object App extends ZIOAppDefault {

  private val loggerLayer   = Runtime.removeDefaultLoggers >>> SLF4J.slf4j
  private val metricsConfig = ZLayer.succeed(MetricsConfig(1.seconds))
  private val publisher     = DefaultJvmMetrics.live.orDie >+> ZLayer.make[PrometheusPublisher](
    metricsConfig,
    prometheusLayer,
    publisherLayer
  )

  private val httpApp = for {
    exampleApi <- ZIO.service[SomeController]
    _          <- Server.serve(exampleApi.routes)
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
      // publisher
      publisher,
      // Controller
      SomeController.live,
      // Server
      zioSeverConfig,
      Server.live,
      loggerLayer
    )
}
