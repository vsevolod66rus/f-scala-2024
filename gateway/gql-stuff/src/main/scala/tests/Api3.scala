package ru.sskie.vpered.gql
package tests

import tests.Data._

import zio.query.{DataSource, Request, ZQuery}

object Api3 {

  type MyQuery[A] = ZQuery[Any, Nothing, A]

  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => MyQuery[List[OrderView]])

  case class OrderView(id: OrderId, customer: MyQuery[Customer], products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: MyQuery[ProductDetailsView], quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: MyQuery[Brand])

  def resolver(dbService: DBService): Query = {

    case class GetCustomer(id: CustomerId) extends Request[Nothing, Customer]
    val CustomerDataSource: DataSource[Any, GetCustomer] =
      DataSource.fromFunctionZIO("CustomerDataSource")(req => dbService.getCustomer(req.id))
    def getCustomer(id: CustomerId): MyQuery[Customer]   = ZQuery.fromRequest(GetCustomer(id))(CustomerDataSource)

    case class GetProduct(id: ProductId) extends Request[Nothing, Product]
    val ProductDataSource: DataSource[Any, GetProduct] =
      DataSource.fromFunctionZIO("ProductDataSource")(req => dbService.getProduct(req.id))
    def getProduct(id: ProductId): MyQuery[Product]    = ZQuery.fromRequest(GetProduct(id))(ProductDataSource)

    case class GetBrand(id: BrandId) extends Request[Nothing, Brand]
    val BrandDataSource: DataSource[Any, GetBrand] =
      DataSource.fromFunctionZIO("BrandDataSource")(req => dbService.getBrand(req.id))
    def getBrand(id: BrandId): MyQuery[Brand]      = ZQuery.fromRequest(GetBrand(id))(BrandDataSource)

    def getOrders(count: Int): MyQuery[List[OrderView]] =
      ZQuery
//        .fromZIO(dbService.getLastOrders(count))
        .fromZIO(dbService.getLastOrdersReality(count))
        .map { orders =>
          orders.map { order =>
            val products = order.productsQuantity.map { productQuantity =>
              val products: MyQuery[ProductDetailsView] = getProduct(productQuantity.id).map { p =>
                val brand: MyQuery[Brand] = getBrand(p.brandId)
                ProductDetailsView(p.name, p.description, brand)
              }
              ProductOrderView(productQuantity.id, products, productQuantity.quantity)
            }
            OrderView(order.id, getCustomer(order.customerId), products)
          }
        }

    Query(args => getOrders(args.count))
  }

}
