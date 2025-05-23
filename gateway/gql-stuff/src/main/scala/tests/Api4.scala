package ru.sskie.vpered.gql
package tests

import tests.Data._

import zio.Chunk
import zio.query.{DataSource, Request, ZQuery}

object Api4 {

  type MyQuery[A] = ZQuery[Any, Nothing, A]

  case class QueryArgs(count: Int)
  case class Query(orders: QueryArgs => MyQuery[List[OrderView]])

  case class OrderView(id: OrderId, customer: MyQuery[Customer], products: List[ProductOrderView])
  case class ProductOrderView(id: ProductId, details: MyQuery[ProductDetailsView], quantity: Int)
  case class ProductDetailsView(name: String, description: String, brand: MyQuery[Brand])

  def resolver(dbService: DBService): Query = {

    case class GetCustomer(id: CustomerId) extends Request[Nothing, Customer]
    val CustomerDataSource: DataSource[Any, GetCustomer] =
      DataSource.fromFunctionBatchedZIO("CustomerDataSource")(requests =>
        dbService.getCustomers(requests.map(_.id).toList).map(Chunk.fromIterable)
      )
    def getCustomer(id: CustomerId): MyQuery[Customer]   = ZQuery.fromRequest(GetCustomer(id))(CustomerDataSource)

    case class GetProduct(id: ProductId) extends Request[Nothing, Product]
    val ProductDataSource: DataSource[Any, GetProduct] =
      DataSource.fromFunctionBatchedZIO("ProductDataSource")(requests =>
        dbService.getProducts(requests.map(_.id).toList).map(Chunk.fromIterable)
      )
    def getProduct(id: ProductId): MyQuery[Product]    = ZQuery.fromRequest(GetProduct(id))(ProductDataSource)

    case class GetBrand(id: BrandId) extends Request[Nothing, Brand]
    val BrandDataSource: DataSource[Any, GetBrand] =
      DataSource.fromFunctionBatchedZIO("BrandDataSource")(requests =>
        dbService.getBrands(requests.map(_.id).toList).map(Chunk.fromIterable)
      )
    def getBrand(id: BrandId): MyQuery[Brand]      = ZQuery.fromRequest(GetBrand(id))(BrandDataSource)

    def getOrders(count: Int): MyQuery[List[OrderView]] =
      ZQuery
        .fromZIO(dbService.getLastOrdersReality(count))
        .map(_.map(order => OrderView(order.id, getCustomer(order.customerId), getProducts(order.productsQuantity))))

    def getProducts(products: List[ProductQuantity]): List[ProductOrderView] =
      products.map { case ProductQuantity(productId, quantity) =>
        ProductOrderView(
          productId,
          getProduct(productId).map(p => ProductDetailsView(p.name, p.description, getBrand(p.brandId))),
          quantity
        )
      }

    Query(args => getOrders(args.count))
  }

}
