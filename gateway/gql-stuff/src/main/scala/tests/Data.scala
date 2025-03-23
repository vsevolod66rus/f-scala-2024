package ru.sskie.vpered.gql
package tests

object Data {
  type ProductId  = Int
  type OrderId    = Int
  type CustomerId = Int
  type BrandId    = Int

  case class Brand(id: BrandId, name: String)
  case class Product(id: ProductId, name: String, description: String, brandId: BrandId)
  case class ProductQuantity(id: ProductId, quantity: Int)
  case class Customer(id: CustomerId, firstName: String, lastName: String)
  case class Order(id: OrderId, customerId: CustomerId, productsQuantity: List[ProductQuantity])
}
