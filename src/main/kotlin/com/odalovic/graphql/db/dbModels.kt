package com.odalovic.graphql.db

class OrderItemEntity(val id: String? = null, val productName: String, var quantity: Int)

class OrderEntity(var id: String? = null, var items: List<OrderItemEntity> = emptyList())