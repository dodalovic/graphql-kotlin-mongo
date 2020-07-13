package com.odalovic.graphql

import com.odalovic.graphql.db.OrderEntity
import com.odalovic.graphql.db.OrderService
import org.eclipse.microprofile.graphql.*

@GraphQLApi
class OrdersApi(private val orderService: OrderService) {

    @Query("order")
    fun getOrderById(@Name("orderId") orderId: String) = orderService.findById(orderId)
        ?.let { order ->
            Order(
                order.id ?: error("Order without ID!!!"),
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
    fun createOrder(newOrder: NewOrder) = orderService.createOrder(newOrder)

    @Mutation("deleteOrderItem")
    fun deleteOrderItem(orderItemId: String) = orderService.deleteOrderItem(orderItemId)
}