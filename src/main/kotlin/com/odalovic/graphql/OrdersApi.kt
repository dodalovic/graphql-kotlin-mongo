package com.odalovic.graphql

import com.odalovic.graphql.db.OrderEntity
import com.odalovic.graphql.db.OrderService
import org.eclipse.microprofile.graphql.*

@GraphQLApi
class OrdersApi(private val orderService: OrderService) {

    @Query("order")
    @Description("Returns the order by given ID, or null if the order doesn't exist")
    fun getOrderById(@Description("Unique order identifier") @Name("orderId") orderId: String) =
        orderService.findById(orderId)
            ?.let { order ->
                Order(
                    order.id ?: error("Order without ID"),
                    order.items.map { item ->
                        OrderItem(
                            item.id ?: error("Order item without ID!!!"),
                            item.productName,
                            item.quantity
                        )
                    })
            }

    fun items(@Source orderEntity: OrderEntity) =
        orderEntity.items.map { OrderItem(it.id ?: error("Order without ID!!!"), it.productName, it.quantity) }

    @Mutation("createOrder")
    @Description("Creates a new order comprising given items")
    fun createOrder(@Description("New order definition") newOrder: NewOrder) = orderService.createOrder(newOrder)

    @Mutation("deleteOrderItem")
    @Description("Deletes particular order item by given item ID")
    fun deleteOrderItem(@Description("Unique order item identifier") orderItemId: String) =
        orderService.deleteOrderItem(orderItemId)
}