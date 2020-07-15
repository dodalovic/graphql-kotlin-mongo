package com.odalovic.graphql

import com.odalovic.graphql.db.OrderService
import org.eclipse.microprofile.graphql.*

@GraphQLApi
class OrdersApi(private val orderService: OrderService) {

    @Query("order")
    @Description("Returns the order by given ID, or null if the order doesn't exist")
    fun getOrderById(@Description("Unique order identifier") @Name("orderId") orderId: String) =
        orderService.findById(orderId)?.let { Order(it.id!!) } ?: error("No such order $orderId")

    fun items(@Source order: Order) = orderService.getItems(order.id)

    @Mutation("createOrder")
    @Description("Creates a new order comprising given items")
    fun createOrder(@Description("New order definition") newOrder: NewOrder) = orderService.createOrder(newOrder)

    @Mutation("addOrderItem")
    @Description("Merges (updates quantity) or adds a new order item to an existing order")
    fun upsertOrderItem(orderId: String, orderItem: NewOrderItem) =
        orderService.upsertOrderItem(orderId, orderItem).let { order ->
            Order(
                id = order.id!!,
                items = order.items.map { item ->
                    OrderItem(
                        id = item.id!!,
                        product = item.productName,
                        quantity = item.quantity
                    )
                })
        }

    @Description("Delete the order by ID")
    @Mutation
    fun deleteOrder(@Description("Order ID to identify the order to be deleted") orderId: String) =
        orderService.deleteOrder(orderId)

    @Mutation("deleteOrderItem")
    @Description("Deletes particular order item by given item ID")
    fun deleteOrderItem(@Description("Unique order item identifier") orderItemId: String) =
        orderService.deleteOrderItem(orderItemId)
}