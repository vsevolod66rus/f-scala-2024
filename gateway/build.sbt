import Dependencies.*

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val scalaFmtSettings = Seq(
  scalafmtOnCompile      := true,
  scalafmtLogOnEachError := true
)

lazy val gatewayApp = (project in file("."))
  .in(file("gateway-app"))
  .settings(
    name             := "gateway-app",
    idePackagePrefix := Some("ru.sskie.vpered")
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(scalaFmtSettings)
  .settings(
    libraryDependencies += zio,
    libraryDependencies += zioStreams,
    libraryDependencies ++= zioConfig,
    libraryDependencies ++= logging,
    libraryDependencies ++= circe,
    libraryDependencies ++= monocle,
    libraryDependencies ++= doobie,
    libraryDependencies += cats3Interop,
    libraryDependencies += pgDriver,
    libraryDependencies += chimney,
    libraryDependencies += h2,
    libraryDependencies ++= circe,
    libraryDependencies += tapirCore,
    libraryDependencies ++= tapirServer,
    libraryDependencies ++= tapirCirce,
    libraryDependencies ++= tapirZio,
    libraryDependencies ++= metrics,
    libraryDependencies += zioQuery
  )

lazy val staffApp = (project in file("."))
  .in(file("staff-app"))
  .settings(
    name             := "staff-app",
    idePackagePrefix := Some("ru.sskie.vpered.staff")
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(scalaFmtSettings)
  .settings(
    libraryDependencies += zio,
    libraryDependencies += zioStreams,
    libraryDependencies ++= zioConfig,
    libraryDependencies ++= logging,
    libraryDependencies ++= circe,
    libraryDependencies ++= monocle,
    libraryDependencies ++= doobie,
    libraryDependencies += cats3Interop,
    libraryDependencies += pgDriver,
    libraryDependencies += chimney,
    libraryDependencies += h2,
    libraryDependencies ++= circe,
    libraryDependencies += tapirCore,
    libraryDependencies ++= tapirServer,
    libraryDependencies ++= tapirCirce,
    libraryDependencies ++= tapirZio,
    libraryDependencies ++= metrics,
    libraryDependencies += zioQuery
  )

lazy val techApp = (project in file("."))
  .in(file("tech-app"))
  .settings(
    name             := "tech-app",
    idePackagePrefix := Some("ru.sskie.vpered.tech")
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(scalaFmtSettings)
  .settings(
    libraryDependencies += zio,
    libraryDependencies += zioStreams,
    libraryDependencies ++= zioConfig,
    libraryDependencies ++= logging,
    libraryDependencies ++= circe,
    libraryDependencies ++= monocle,
    libraryDependencies ++= doobie,
    libraryDependencies += cats3Interop,
    libraryDependencies += pgDriver,
    libraryDependencies += chimney,
    libraryDependencies += h2,
    libraryDependencies ++= circe,
    libraryDependencies += tapirCore,
    libraryDependencies ++= tapirServer,
    libraryDependencies ++= tapirCirce,
    libraryDependencies ++= tapirZio,
    libraryDependencies ++= metrics,
    libraryDependencies += zioQuery
  )

lazy val foodApp = (project in file("."))
  .in(file("food-app"))
  .settings(
    name             := "food-app",
    idePackagePrefix := Some("ru.sskie.vpered.food")
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(scalaFmtSettings)
  .settings(
    libraryDependencies += zio,
    libraryDependencies += zioStreams,
    libraryDependencies ++= zioConfig,
    libraryDependencies ++= logging,
    libraryDependencies ++= circe,
    libraryDependencies ++= monocle,
    libraryDependencies ++= doobie,
    libraryDependencies += cats3Interop,
    libraryDependencies += pgDriver,
    libraryDependencies += chimney,
    libraryDependencies += h2,
    libraryDependencies ++= circe,
    libraryDependencies += tapirCore,
    libraryDependencies ++= tapirServer,
    libraryDependencies ++= tapirCirce,
    libraryDependencies ++= tapirZio,
    libraryDependencies ++= metrics,
    libraryDependencies += zioQuery
  )

lazy val gqlStuff = (project in file("."))
  .in(file("gql-stuff"))
  .settings(
    name             := "gql-stuff",
    idePackagePrefix := Some("ru.sskie.vpered.gql")
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(scalaFmtSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.github.ghostdogpr"         %% "caliban"                       % "2.9.1",
      "com.github.ghostdogpr"         %% "caliban-http4s"                % "2.9.1",
      "com.github.ghostdogpr"         %% "caliban-client"                % "2.9.1",
      "org.http4s"                    %% "http4s-blaze-server"           % "0.23.17",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.10.2",
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"              % "1.11.13"
    )
  )
  .enablePlugins(CalibanPlugin)
