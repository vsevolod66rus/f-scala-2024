package ru.sskie.vpered.gql
package tests

import tests.Data._

import zio.{Ref, UIO}

class DBService(dbHits: Ref[Int]) {

  def getBrand(id: BrandId): UIO[Brand] =
    dbHits.update(_ + 1).as(Brand(id, "some brand"))

  def getProduct(id: ProductId): UIO[Product] =
    dbHits.update(_ + 1).as(Product(id, "some product", "product description", id % 10))

  def getCustomer(id: CustomerId): UIO[Customer] =
    dbHits.update(_ + 1).as(Customer(id, "first name", "last name"))

  def getLastOrders(count: Int): UIO[List[Order]] =
    dbHits
      .update(_ + 1)
      .as {
        List.range(0, count).map { id =>
          Order(id, id % 3, List(ProductQuantity(id % 5, 1), ProductQuantity(id % 2, 1)))
        }
      }

  def getLastOrdersReality(count: Int): UIO[List[Order]] =
    dbHits.update(_ + 1).as {
      val rand = scala.util.Random
      List.range(0, count).map { id =>
        Order(id, rand.nextInt(), List(ProductQuantity(rand.nextInt(), 1), ProductQuantity(rand.nextInt(), 1)))
      }
    }

  def getCustomers(ids: List[CustomerId]): UIO[List[Customer]] =
    dbHits.update(_ + 1).as(ids.map(id => Customer(id, "first name", "last name")))

  def getBrands(ids: List[BrandId]): UIO[List[Brand]] =
    dbHits.update(_ + 1).as(ids.map(id => Brand(id, "some brand")))

  def getProducts(ids: List[ProductId]): UIO[List[Product]] =
    dbHits.update(_ + 1).as(ids.map(id => Product(id, "some product", "product description", id % 10)))

  val hits: UIO[Int] = dbHits.get
}

object DBService {
  def apply(): UIO[DBService] =
    for {
      dbHits <- Ref.make(0)
    } yield new DBService(dbHits)
}
