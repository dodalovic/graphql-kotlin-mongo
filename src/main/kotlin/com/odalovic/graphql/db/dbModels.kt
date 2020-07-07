package com.odalovic.graphql.db

class OrderItemEntity(val id: String? = null, val productName: String, val quantity: Int)

class OrderEntity(var id: String? = null, val items: List<OrderItemEntity> = emptyList())
