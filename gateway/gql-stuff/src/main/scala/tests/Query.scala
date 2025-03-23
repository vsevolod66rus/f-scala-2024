package ru.sskie.vpered.gql
package tests

object Query {
  val orders: String = """
                         |{
                         |  orders(count: 20) {
                         |    id
                         |    customer {
                         |      id
                         |      firstName
                         |      lastName
                         |    }
                         |    products {
                         |      id
                         |      quantity
                         |      details {
                         |        name
                         |        description
                         |      }
                         |    }
                         |  }
                         |}
                         |""".stripMargin

  val ordersWithBrands: String = """
                                   |{
                                   |  orders(count: 20) {
                                   |    id
                                   |    customer {
                                   |      id
                                   |      firstName
                                   |      lastName
                                   |    }
                                   |    products {
                                   |      id
                                   |      quantity
                                   |      details {
                                   |        name
                                   |        description
                                   |        brand {
                                   |          id
                                   |          name
                                   |        }
                                   |      }
                                   |    }
                                   |  }
                                   |}
                                   |""".stripMargin
}
