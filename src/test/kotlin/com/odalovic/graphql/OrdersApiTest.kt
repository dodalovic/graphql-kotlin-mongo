package com.odalovic.graphql

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

@NoArgConstructor
data class CreateOrder(var createdOrderId: String, var errors: List<String>)

@NoArgConstructor
data class Data(var createOrder: CreateOrder)

@NoArgConstructor
data class MyCreateOrderResponse(var data: Data)

@QuarkusTest
@QuarkusTestResource(MongoDBResource::class)
class OrdersApiTest {

    @Test
    fun `creates new order`() {
        val createOrderResponse = given()
            .headers(mutableMapOf("Accept" to "application/json", "Content-Type" to "application/json"))
            .body(
                """{
                  "query": "mutation CreateOrder { createOrder(newOrder:{ items: [ { productName: \"a\", quantity: 2 }]}) { createdOrderId\nerrors } }"
                }""".trimIndent()
            ).`when`().post("/graphql")
            .then()
            .statusCode(200)
            .extract().`as`(MyCreateOrderResponse::class.java)
        val (createdOrderId, errors) = createOrderResponse.data.createOrder
        assertTrue(createdOrderId.isNotBlank())
        assertTrue(errors.isEmpty())

        given()
            .headers(mutableMapOf("Accept" to "application/json", "Content-Type" to "application/json"))
            .body(
                """{
                  "query": "query GetOrder {\n  order(orderId: \"$createdOrderId\") {\n    order {\n      id\n      items{\n        id\n        productName\n        quantity\n      }\n    }\n  }\n}\n",
                  "variables": null,
                  "operationName": "GetOrder"
              }""".trimIndent()
            ).`when`().post("/graphql")
            .then()
            .statusCode(200)
        // TODO: assert on something like: {"data":{"order":{"order":{"id":"5f0dd4cf82b7a0360997a89b","items":[{"id":"0ab7b5ca-5b10-42e9-9cc9-a3dddac55310","productName":"x","quantity":1},{"id":"067f0d4f-4477-4682-b09f-82ac8f9eeb01","productName":"y","quantity":2}]}}}}
    }
}