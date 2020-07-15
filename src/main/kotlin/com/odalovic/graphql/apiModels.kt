package com.odalovic.graphql

import org.eclipse.microprofile.graphql.Description

@Description("Order item output definition")
data class OrderItem(
    @field:Description("Order item ID") val id: String,
    @field:Description("Product code") val product: String,
    @field:Description("Product quantity") val quantity: Int
)

@Description("Order output definition")
data class Order(
    @field:Description("Order ID") val id: String,
    @field:Description("Order items") val items: List<OrderItem>? = null
)

@NoArgConstructor
@Description("Order item definition")
data class NewOrderItem(
    @field:Description("Product code") var product: String,
    @field:Description("Product quantity") var quantity: Int
)

@NoArgConstructor
@Description("New order definition")
data class NewOrder(@Description("Order items comprising order") var items: List<NewOrderItem>)
