package com.odalovic.graphql.db

import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Updates.set
import com.odalovic.graphql.NewOrder
import com.odalovic.graphql.NewOrderItem
import com.odalovic.graphql.OrderItem
import org.bson.BsonObjectId
import org.bson.types.ObjectId
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default


@ApplicationScoped
class OrderService(@field:Default val mongoClient: MongoClient) {

    fun findById(orderId: String): OrderEntity? {
        val objectId = try {
            ObjectId(orderId)
        } catch (e: IllegalArgumentException) {
            return null
        }
        return ordersCollection().find(eq("_id", objectId)).projection(Projections.include("_id")).firstOrNull()
    }

    private fun ordersCollection() =
        mongoClient.getDatabase("order-management").getCollection("orders", OrderEntity::class.java)

    fun createOrder(newOrder: NewOrder): String {
        val result = ordersCollection().insertOne(OrderEntity(items = newOrder.items.map {
            OrderItemEntity(
                productName = it.product,
                quantity = it.quantity
            )
        }))
        return (result.insertedId as BsonObjectId).value.toString()
    }

    fun deleteOrderItem(orderItemId: String): Long {
        val findExistingOrderFilter = eq("items._id", orderItemId)
        val existingOrder =
            ordersCollection().find(findExistingOrderFilter).first()
                ?: error("No such order containing item with ID $orderItemId")
        return ordersCollection().updateOne(
            findExistingOrderFilter, set("items",
                existingOrder.items.filterNot { it.id == orderItemId })
        ).modifiedCount
    }

    fun deleteOrder(orderId: String): Boolean {
        return ordersCollection().deleteOne(eq("_id", ObjectId(orderId))).deletedCount.toInt() == 1
    }

    fun upsertOrderItem(orderId: String, newOrderItem: NewOrderItem): OrderEntity {
        val existingOrderFilter = eq("_id", ObjectId(orderId))
        val ordersCollection = ordersCollection()
        val order =
            ordersCollection.find(existingOrderFilter).first() ?: error("No such order with ID $orderId")
        var alreadyExists = false
        val futureItems = order.items.map { itemEntity ->
            if (itemEntity.productName == newOrderItem.product) {
                itemEntity.quantity += newOrderItem.quantity
                alreadyExists = true
                itemEntity
            } else {
                itemEntity
            }
        }.toMutableList()
        if (!alreadyExists) {
            futureItems += OrderItemEntity(
                id = UUID.randomUUID().toString(),
                productName = newOrderItem.product,
                quantity = newOrderItem.quantity
            )

        }
        ordersCollection.updateOne(existingOrderFilter, set("items", futureItems))
        return ordersCollection.find(existingOrderFilter).first()!!
    }

    fun getItems(orderId: String): List<OrderItem> {
        return (ordersCollection().find(eq("_id", ObjectId(orderId))).first() ?: error("No such order")).items.map {
            OrderItem(
                id = it.id!!,
                product = it.productName,
                quantity = it.quantity
            )
        }
    }

}
