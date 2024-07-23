import Dependencies.Versions.*
import sbt.*

object Dependencies {

  object Versions {
    lazy val zioVersion          = "2.0.2"
    lazy val zioLoggingVersion   = "2.1.2"
    lazy val zioConfigVersion    = "3.0.1"
    lazy val logbackVersion      = "1.4.3"
    lazy val log4jVersion        = "2.19.0"
    lazy val circeVersion        = "0.14.1"
    lazy val monocleVersion      = "3.1.0"
    lazy val poiVersion          = "5.2.3"
    lazy val typeDBVersion       = "2.11.1"
    lazy val doobieVersion       = "1.0.0-RC2"
    lazy val cats3InteropVersion = "3.3.0"
    lazy val pgDriverVersion     = "42.5.0"
    lazy val chimneyVersion      = "0.6.2"
    lazy val h2Version           = "2.1.214"
    lazy val tapirVersion        = "1.10.13"
    lazy val apiSpecVersion      = "0.4.0"
  }

  lazy val zio: ModuleID        = "dev.zio" %% "zio"         % zioVersion
  lazy val zioStreams: ModuleID = "dev.zio" %% "zio-streams" % zioVersion
  lazy val zioQuery = "dev.zio" %% "zio-query" % "0.7.4"

  lazy val logging: Seq[ModuleID] = Seq(
    "dev.zio"                 %% "zio-logging-slf4j" % zioLoggingVersion,
    "ch.qos.logback"           % "logback-classic"   % logbackVersion,
    "org.apache.logging.log4j" % "log4j-core"        % log4jVersion
  )

  lazy val zioConfig: Seq[ModuleID] = Seq(
    "dev.zio" %% "zio-config",
    "dev.zio" %% "zio-config-magnolia",
    "dev.zio" %% "zio-config-typesafe"
  ).map(_ % zioConfigVersion)

  lazy val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser",
//    "io.circe" %% "circe-yaml"
  ).map(_ % circeVersion)

  lazy val circeExtras = "io.circe" %% "circe-generic-extras" % circeVersion

  lazy val monocle: Seq[ModuleID] = Seq(
    "dev.optics" %% "monocle-core"  % monocleVersion,
    "dev.optics" %% "monocle-macro" % monocleVersion
  )

  lazy val typeDB: ModuleID = "com.vaticle.typedb" % "typedb-client" % typeDBVersion

  lazy val doobie: Seq[ModuleID] = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-hikari",
    "org.tpolecat" %% "doobie-postgres"
  ).map(_ % doobieVersion)

  lazy val cats3Interop = "dev.zio" %% "zio-interop-cats" % cats3InteropVersion

  lazy val pgDriver = "org.postgresql" % "postgresql" % pgDriverVersion

  lazy val chimney = "io.scalaland" %% "chimney" % chimneyVersion

  lazy val h2 = "com.h2database" % "h2" % h2Version

  lazy val tapirCirce = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe",
    "com.softwaremill.sttp.tapir" %% "tapir-enumeratum"
  ).map(_ % tapirVersion)

  lazy val tapirCore = "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion

  lazy val tapirServer = Seq(
    "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"      % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"     % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle" % tapirVersion,
//    "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"      % apiSpecVersion
  )

  lazy val tapirZio = Seq(
    "com.softwaremill.sttp.tapir" %% "tapir-zio"               % tapirVersion,
    "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server"   % tapirVersion,
//    "com.softwaremill.sttp.tapir" %% "tapir-zio-http4s-server" % "1.0.0-RC1"
  )

  lazy val metrics = Seq(
    "dev.zio"                     %% "zio-metrics-connectors"            % "2.3.1",
    "dev.zio"                     %% "zio-metrics-connectors-prometheus" % "2.3.1",
    "com.softwaremill.sttp.tapir" %% "tapir-zio-metrics"                 % "1.10.13"
  )

}
