package ru.sskie.vpered.food

import controllers.FoodController
import models.configs.{AppConfig, ServerConfig}
import repos.FoodRepo
import services.FoodService

import zio.http.Server
import zio.logging.backend.SLF4J
import zio.metrics.connectors.prometheus.{PrometheusPublisher, prometheusLayer, publisherLayer}
import zio.{ExitCode, IO, Runtime, ZIO, ZIOAppDefault, ZLayer, durationInt}
import zio.metrics.connectors.MetricsConfig
import zio.metrics.jvm.DefaultJvmMetrics

object FoodApp extends ZIOAppDefault {

  private val loggerLayer   = Runtime.removeDefaultLoggers >>> SLF4J.slf4j
  private val metricsConfig = ZLayer.succeed(MetricsConfig(1.seconds))
  private val publisher     = DefaultJvmMetrics.live.orDie >+> ZLayer.make[PrometheusPublisher](
    metricsConfig,
    prometheusLayer,
    publisherLayer
  )

  private val httpApp = for {
    exampleApi <- ZIO.service[FoodController]
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
      FoodRepo.live,
      // Service
      FoodService.live,
      // publisher
      publisher,
      // Controller
      FoodController.live,
      // Server
      zioSeverConfig,
      Server.live,
      loggerLayer
    )
}
