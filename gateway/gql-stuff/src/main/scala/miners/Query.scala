package ru.sskie.vpered.gql
package miners

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
}
