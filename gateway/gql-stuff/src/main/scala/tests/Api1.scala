package ru.sskie.vpered.gql
package tests

import tests.Data._

import zio.{UIO, ZIO}

object Api1 extends {

  // кейс классы для определения схемы GraphQL
  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => UIO[List[OrderView]])

  case class OrderView(id: OrderId, customer: Customer, products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: ProductDetailsView, quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: Brand)

  def resolver(dbService: DBService): Query = {

    def getOrders(count: Int): UIO[List[OrderView]] =
      dbService
        .getLastOrders(count)
        .flatMap { orders =>
          ZIO.foreach(orders) { order =>
            for {
              customer <- dbService.getCustomer(order.customerId)
              products <- ZIO.foreach(order.productsQuantity) { productQuantity =>
                            dbService
                              .getProduct(productQuantity.id)
                              .flatMap { product =>
                                dbService
                                  .getBrand(product.brandId)
                                  .map(brand => ProductDetailsView(product.name, product.description, brand))
                              }
                              .map(details => ProductOrderView(productQuantity.id, details, productQuantity.quantity))
                          }
            } yield OrderView(order.id, customer, products)
          }
        }

    Query(args => getOrders(args.count))
  }

}
