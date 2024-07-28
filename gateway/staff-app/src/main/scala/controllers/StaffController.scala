package ru.sskie.vpered.staff
package controllers

import models.domain.Staff
import services.StaffService
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

trait StaffController {
  def routes: Routes[Any, Response]
}

object StaffController {
  val live: URLayer[PrometheusPublisher with StaffService, StaffController] =
    ZLayer.fromFunction(StaffControllerImpl.apply _)
}

final case class StaffControllerImpl(publisher: PrometheusPublisher, staffService: StaffService)
    extends StaffController {

  private val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint.in("api" / "v1")

  // test
  val testEndpoint =
    baseEndpoint
      .tag("Staff Service")
      .get
      .in("staff")
      .in(path[String]("name"))
      .errorOut(stringBody)
      .out(jsonBody[Staff])
      .zServerLogic(name =>
        ZIO
          .succeed(staffService.getStaff(name))
          .flatten
          .mapError(t => t.toString)
      )

  val metricEndpoint =
    baseEndpoint
      .tag("Staff Service")
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
        "Staff Service",
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
