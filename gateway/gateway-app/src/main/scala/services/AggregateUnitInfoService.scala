package ru.sskie.vpered
package services

import models.domain.{AggregateUnitInfoDTO, Food, Staff, StaffUnit, Tech}

import zio._
import zio.http.{Client, URL}

trait AggregateUnitInfoService {
  def simpleMethod(name: String): Task[AggregateUnitInfoDTO]

}

object AggregateUnitInfoService {
  val live: URLayer[Client, AggregateUnitInfoService] = ZLayer.fromFunction(AggregateUnitInfoServiceImpl.apply _)
}

final case class AggregateUnitInfoServiceImpl(client: Client) extends AggregateUnitInfoService {

  override def simpleMethod(name: String): Task[AggregateUnitInfoDTO] =
    ZIO.scoped {
      for {
//        staffInfo <- client
//                       .url(URL.decode("http://0.0.0.0:1489/api/v1/staff").toOption.get)
//                       .get("123")
//                       .map(_.body.to[Staff])
//                       .orElse(ZIO.succeed(Staff(Vector.empty)))
//                       .catchAll { e =>
//                         for {
//                           _  <- ZIO.logInfo(e.toString)
//                           res = Staff(units = Vector.empty[StaffUnit])
//                         } yield res
//                       }
        staffResp <- client.url(URL.decode("http://0.0.0.0:1489/api/v1/staff").toOption.get).get("123")
        staffInfo <- staffResp.body.to[Staff]
        techResp  <- client.url(URL.decode("http://0.0.0.0:1490/api/v1/tech").toOption.get).get("123")
        techInfo  <- techResp.body.to[Tech]
        foodResp  <- client.url(URL.decode("http://0.0.0.0:1491/api/v1/food").toOption.get).get("123")
        foodInfo  <- foodResp.body.to[Food]
      } yield AggregateUnitInfoDTO(staff = staffInfo.units, tech = techInfo.units, food = foodInfo.units)
    }

}
