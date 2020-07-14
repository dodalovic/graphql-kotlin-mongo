package com.odalovic.graphql.db

import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set
import com.odalovic.graphql.NewOrder
import org.bson.BsonObjectId
import org.bson.types.ObjectId
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
        return ordersCollection().find(eq("_id", objectId)).firstOrNull()
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

}
