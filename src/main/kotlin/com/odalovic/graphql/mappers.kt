package com.odalovic.graphql

import com.odalovic.graphql.businessmodels.NewOrderItem
import com.odalovic.graphql.db.OrderEntity

fun OrderEntity.toBusinessModel(): com.odalovic.graphql.businessmodels.Order {
    return com.odalovic.graphql.businessmodels.Order(
        id = this.id!!,
        items = this.items.map { item ->
            com.odalovic.graphql.businessmodels.OrderItem(
                id = item.id!!,
                productName = item.productName,
                quantity = item.quantity
            )
        })
}

fun com.odalovic.graphql.businessmodels.OrderItem.toGraphqlModel() = OrderItem(id, productName, quantity)

fun com.odalovic.graphql.businessmodels.Order.toGraphqlModel() = Order(id = this.id, items = this.items.map { item ->
    OrderItem(
        id = item.id, productName = item.productName, quantity = item.quantity
    )
})



fun NewOrder.toBusinessModel() =
    com.odalovic.graphql.businessmodels.NewOrder(this.items.map { item ->
        NewOrderItem(
            item.productName,
            quantity = item.quantity
        )
    })

fun com.odalovic.graphql.NewOrderItem.toBusinessModel() =
    NewOrderItem(this.productName, this.quantity)