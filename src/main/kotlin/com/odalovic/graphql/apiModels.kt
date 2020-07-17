package com.odalovic.graphql

import org.eclipse.microprofile.graphql.Description

@Description("Order item output definition")
data class OrderItem(
    @field:Description("Order item ID") val id: String,
    @field:Description("Product name") val productName: String,
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
    @field:Description("Product name") var productName: String,
    @field:Description("Product quantity") var quantity: Int
)

class CreateOrderResponse(var createdOrderId: String? = null, var errors: List<String> = emptyList())
class DeleteOrderResponse(var totalDeletedCount: Int? = null, var errors: List<String> = emptyList())
class DeleteOrderItemResponse(var totalDeletedCount: Int? = null, var errors: List<String> = emptyList())
class GetOrderResponse(var order: Order? = null, var errors: List<String> = emptyList())

@NoArgConstructor
@Description("New order definition")
data class NewOrder(@Description("Order items comprising order") var items: List<NewOrderItem>)
