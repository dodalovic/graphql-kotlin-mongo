package com.odalovic.graphql

import com.odalovic.graphql.Outcome.*
import org.eclipse.microprofile.graphql.*

@GraphQLApi
class OrdersApi(private val orderService: OrderService) {

    @Query("order")
    @Description("Returns the order by given ID, or null if the order doesn't exist")
    fun getOrderById(@Description("Unique order identifier") @Name("orderId") orderId: String): GetOrderResponse {
        return when (val result = orderService.findById(orderId)) {
            is Success -> GetOrderResponse(result.value.toGraphqlModel())
            is Error -> GetOrderResponse(errors = listOf(result.message))
        }.exhaustive
    }

    fun items(@Source order: Order) = when (val result = orderService.getItems(order.id)) {
        is Success -> result.value.map { it.toGraphqlModel() }
        is Error -> {
            System.err.println("There was an error getting the items for order ${order.id}: ${result.message} : ${result.cause?.message}")
            emptyList()
        }
    }

    @Mutation("createOrder")
    @Description("Creates a new order comprising given items")
    fun createOrder(@Description("New order definition") newOrder: NewOrder): CreateOrderResponse {
        return when (val result = orderService.createOrder(newOrder.toBusinessModel())) {
            is Success -> CreateOrderResponse(result.value)
            is Error -> CreateOrderResponse(errors = listOf(result.message))
        }.exhaustive
    }

    @Mutation("addOrderItem")
    @Description("Merges (updates quantity) or adds a new order item to an existing order")
    fun upsertOrderItem(orderId: String, orderItem: NewOrderItem) =
        when (val result = orderService.upsertOrderItem(orderId, orderItem.toBusinessModel())) {
            is Success -> GetOrderResponse(result.value.toGraphqlModel())
            is Error -> GetOrderResponse(errors = listOf(result.message))
        }.exhaustive

    @Description("Delete the order by ID")
    @Mutation
    fun deleteOrder(@Description("Order ID to identify the order to be deleted") orderId: String) =
        when (val result = orderService.deleteOrder(orderId)) {
            is Success -> DeleteOrderResponse(result.value.toInt())
            is Error -> DeleteOrderResponse(errors = listOf(result.message))
        }.exhaustive

    @Mutation("deleteOrderItem")
    @Description("Deletes particular order item by given item ID")
    fun deleteOrderItem(@Description("Unique order item identifier") orderItemId: String) =
        when (val result = orderService.deleteOrderItem(orderItemId)) {
            is Success -> DeleteOrderItemResponse(result.value.toInt())
            is Error -> DeleteOrderItemResponse(errors = listOf(result.message))
        }.exhaustive
}