package ru.sskie.vpered.staff
package repos

import doobie.Transactor
import doobie.hikari.HikariTransactor
import models.configs.DBConfig
import zio._
import zio.interop.catz._

sealed trait DBTransactor {
  def xa: Transactor[Task]
}

trait AppDBTransactor extends DBTransactor

object DBTransactor {

  val appXaLive: RLayer[DBConfig, AppDBTransactor] = ZLayer.scoped {
    for {
      config <- ZIO.service[DBConfig]
      be     <- ZIO.blockingExecutor
      xaApp  <- makeTransactor(config, be)
    } yield new AppDBTransactor {
      override def xa: Transactor[Task] = xaApp
    }
  }

  private def makeTransactor(config: DBConfig, be: Executor) = for {
    xa <- HikariTransactor
            .newHikariTransactor[Task](
              driverClassName = config.driverClassName,
              url = config.url.getOrElse(s"jdbc:postgresql://${config.host}:${config.port}/${config.db}"),
              user = config.user,
              pass = config.password,
              connectEC = be.asExecutionContext
            )
            .toScopedZIO
            .orDie
    _  <- xa.configure(ds => ZIO.succeed(ds.setMaximumPoolSize(config.maxPoolSize)))
  } yield xa
}
