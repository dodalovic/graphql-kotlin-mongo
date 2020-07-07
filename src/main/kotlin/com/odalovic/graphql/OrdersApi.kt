package com.odalovic.graphql

import com.odalovic.graphql.db.OrderEntity
import com.odalovic.graphql.db.OrderService
import org.eclipse.microprofile.graphql.*
import javax.enterprise.inject.Default
import javax.inject.Inject

@GraphQLApi
class OrdersApi {

    @Inject
    @field:Default
    lateinit var orderService: OrderService

    @Query("order")
    fun getOrderById(@Name("orderId") orderId: String) =
        orderService.findById(orderId)
            ?.let { orderEntity ->
                Order(
                    orderEntity.id ?: error("Order without ID!!!"),
                    orderEntity.items.map { orderItemEntity ->
                        OrderItem(
                            orderItemEntity.id ?: error("Order item without ID!!!"),
                            orderItemEntity.productName,
                            orderItemEntity.quantity
                        )
                    })
            }

    fun items(@Source orderEntity: OrderEntity): List<OrderItem> {
        return orderEntity.items.map { OrderItem(it.id ?: error("Order without ID!!!"), it.productName, it.quantity) }
    }

    @Mutation("createOrder")
    fun createOrder(newOrder: NewOrder) = orderService.createOrder(newOrder)

    @Mutation("deleteOrderItem")
    fun deleteOrderItem(orderItemId: String): Long {
        return orderService.deleteOrderItem(orderItemId)
    }
}