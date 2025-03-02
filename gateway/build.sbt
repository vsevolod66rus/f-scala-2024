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
    idePackagePrefix := Some("ru.sskie.vpered"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
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
    idePackagePrefix := Some("ru.sskie.vpered.gql"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
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
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"              % "1.11.13",
      "com.47deg"                     %% "fetch"                         % "3.1.2"
    )
  )
  .enablePlugins(CalibanPlugin)

lazy val fetchStuff = (project in file("."))
  .in(file("fetch-stuff"))
  .settings(
    name             := "fetch-stuff",
    idePackagePrefix := Some("ru.sskie.vpered.fetch"),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(GitVersioning)
  .settings(scalaFmtSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.47deg"                  %% "fetch"           % "3.1.2",
      "com.47deg"                  %% "fetch-debug"     % "3.1.2",
      "com.github.blemale"         %% "scaffeine"       % "5.3.0",
      "co.fs2"                     %% "fs2-core"        % "3.11.0",
      "ch.qos.logback"              % "logback-classic" % "1.5.16",
      "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.5"
    )
  )
  .enablePlugins(CalibanPlugin)
