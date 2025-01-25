package ru.sskie.vpered.gql
package tests

import tests.Data._

import zio.UIO

object Api2 {

  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => UIO[List[OrderView]])

  case class OrderView(
      id: OrderId,
      customer: UIO[Customer],
      products: List[ProductOrderView]
  )
  case class ProductOrderView(
      id: ProductId,
      details: UIO[ProductDetailsView],
      quantity: Int
  )
  case class ProductDetailsView(
      name: String,
      description: String,
      brand: UIO[Brand]
  )

  def resolver(dbService: DBService): Query = {

    def getOrders(count: Int): UIO[List[OrderView]] =
      dbService
        .getLastOrders(count)
        .map { orders =>
          orders.map { order =>
            val customerF: UIO[Customer]         = dbService.getCustomer(order.customerId)
            val products: List[ProductOrderView] = order.productsQuantity.map { productQuantity =>
              val detailsF: UIO[ProductDetailsView] = dbService
                .getProduct(productQuantity.id)
                .map { product =>
                  val brandF: UIO[Brand] = dbService.getBrand(product.brandId)
                  ProductDetailsView(
                    name = product.name,
                    description = product.description,
                    brand = brandF
                  )
                }
              ProductOrderView(
                id = productQuantity.id,
                details = detailsF,
                quantity = productQuantity.quantity
              )
            }
            OrderView(order.id, customerF, products)
          }
        }

    Query(args => getOrders(args.count))
  }

}
