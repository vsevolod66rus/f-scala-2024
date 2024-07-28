package ru.sskie.vpered.tech
package models.configs

final case class DBConfig(
    host: String,
    port: Int,
    db: String,
    url: Option[String],
    user: String,
    password: String,
    driverClassName: String = "org.postgresql.Driver",
    maxPoolSize: Int = 8
)
