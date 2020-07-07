package com.odalovic.graphql.db

import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters
import com.odalovic.graphql.NewOrder
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
        return ordersCollection().find(Filters.eq("_id", objectId)).firstOrNull()
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
        return result.insertedId?.toString() ?: error("No inserted id!?!?")
    }

    fun deleteOrderItem(orderItemId: String): Long {
        val match =
            ordersCollection().find(Filters.eq("items._id", ObjectId(orderItemId))).first()
                ?: error("NO such order item!")
        println(match)
        val deleteResult = ordersCollection().deleteOne(Filters.eq("items._id", ObjectId(orderItemId)))
        return deleteResult.deletedCount
    }

}
