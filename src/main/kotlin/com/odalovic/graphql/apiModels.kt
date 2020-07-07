package com.odalovic.graphql

data class OrderItem(val id: String, val product: String, val quantity: Int)
data class Order(val id: String, val items: List<OrderItem>? = null)

@NoArgConstructor
data class NewOrderItem(var product: String, var quantity: Int)

@NoArgConstructor
data class NewOrder(var items: List<NewOrderItem>)
