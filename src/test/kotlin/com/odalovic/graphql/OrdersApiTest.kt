package com.odalovic.graphql

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
@QuarkusTestResource(MongoDBResource::class)
class OrdersApiTest {

    @Test
    fun `creates new order`() {
        given()
            .headers(mutableMapOf("Accept" to "application/json", "Content-Type" to "application/json"))
            .body(
                """{
                  "query": "mutation CreateNewOrder {
                    createOrder(newOrder: {items: [{productName: \"A\", quantity: 2}]}) {
                      createdOrderId
                      errors
                    }
                  }
                }""".trimIndent()
            ).`when`().post("/graphql")
            .then()
            .statusCode(200)
            .body(`is`("foo"))
    }

}