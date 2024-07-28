package ru.sskie.vpered.tech
package controllers

import models.domain.Tech
import services.TechService
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import sttp.tapir.PublicEndpoint
import zio._
import zio.http.{Response, Routes}
import zio.metrics.connectors.prometheus.PrometheusPublisher
import sttp.tapir.server.interceptor.metrics.MetricsRequestInterceptor
import sttp.tapir.server.metrics.zio.ZioMetrics
import sttp.tapir.server.model.ValuedEndpointOutput

trait TechController {
  def routes: Routes[Any, Response]
}

object TechController {
  val live: URLayer[PrometheusPublisher with TechService, TechController] =
    ZLayer.fromFunction(TechControllerImpl.apply _)
}

final case class TechControllerImpl(publisher: PrometheusPublisher, techService: TechService) extends TechController {

  private val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint.in("api" / "v1")

  // test
  val testEndpoint =
    baseEndpoint
      .tag("Tech Service")
      .get
      .in("tech")
      .in(path[String]("name"))
      .errorOut(stringBody)
      .out(jsonBody[Tech])
      .zServerLogic(name =>
        ZIO
          .succeed(techService.getStaff(name))
          .flatten
          .mapError(t => t.toString)
      )

  val metricEndpoint =
    baseEndpoint
      .tag("Tech Service")
      .get
      .in("metrics")
      .errorOut(stringBody)
      .out(stringBody)
      .zServerLogic(_ => publisher.get)

  // Docs
  val swaggerEndpoints: List[ZServerEndpoint[Any, Any]] =
    SwaggerInterpreter()
      .fromEndpoints[Task](
        List(
          testEndpoint.endpoint,
          metricEndpoint.endpoint
        ),
        "Tech Service",
        "1.0"
      )

  def failureResponse(msg: String): ValuedEndpointOutput[_] =
    ValuedEndpointOutput(stringBody, msg)

  val metrics: ZioMetrics[Task]                           = ZioMetrics.default[Task]()
  val metricsInterceptor: MetricsRequestInterceptor[Task] = metrics.metricsInterceptor()
  val serverOptions: ZioHttpServerOptions[Any]            =
    ZioHttpServerOptions.customiseInterceptors
      .metricsInterceptor(metricsInterceptor)
      .defaultHandlers(failureResponse)
      .options

  def routes: Routes[Any, Response] =
    ZioHttpInterpreter(serverOptions).toHttp(
      List(
        testEndpoint,
        metricEndpoint
      ) ++ swaggerEndpoints
    )

}
