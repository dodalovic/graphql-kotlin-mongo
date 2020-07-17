package com.odalovic.graphql.businessmodels

class OrderItem(val id: String, val productName: String, val quantity: Int)

class Order(val id: String, val items: List<OrderItem>)

class NewOrderItem(val productName: String, val quantity: Int)

class NewOrder(val items: List<NewOrderItem>)