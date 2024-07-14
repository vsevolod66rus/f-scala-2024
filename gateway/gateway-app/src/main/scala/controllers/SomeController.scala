package ru.sskie.vpered
package controllers

import models.domain.SomeModel
import services.SomeService
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import sttp.tapir.PublicEndpoint
import zio._
import zio.http.HttpApp

trait SomeController {
  def routes: HttpApp[Any, Throwable]
}

object SomeController {
  val live: URLayer[SomeService, SomeController] = ZLayer.fromFunction(SomeControllerImpl.apply _)
}

final case class SomeControllerImpl(dadikService: SomeService) extends SomeController {

  private val baseEndpoint: PublicEndpoint[Unit, Unit, Unit, Any] = endpoint.in("api" / "v1")

  // test
  val testEndpoint =
    baseEndpoint
      .tag("Some")
      .get
      .in("test")
      .in(path[String]("name"))
      .errorOut(stringBody)
      .out(jsonBody[SomeModel])
      .zServerLogic(name =>
        ZIO
          .succeed(dadikService.simpleMethod(name))
          .flatten
          .mapError(t => t.toString)
      )

  // Docs
  val swaggerEndpoints: List[ZServerEndpoint[Any, Any]] =
    SwaggerInterpreter()
      .fromEndpoints[Task](
        List(
          testEndpoint.endpoint
        ),
        "Some",
        "1.0"
      )

  def routes: HttpApp[Any, Throwable] =
    ZioHttpInterpreter().toHttp(
      List(
        testEndpoint
      ) ++ swaggerEndpoints
    )

}
