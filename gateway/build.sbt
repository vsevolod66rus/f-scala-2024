import Dependencies.*

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val scalaFmtSettings = Seq(
  scalafmtOnCompile := true,
  scalafmtLogOnEachError := true
)

lazy val gatewayApp = (project in file("."))
  .in(file("gateway-app"))
  .settings(
    name := "gateway-app",
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
    libraryDependencies ++= tapirZio
  )
