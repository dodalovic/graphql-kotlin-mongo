package com.odalovic.graphql

import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Projections
import com.mongodb.client.model.Updates.set
import com.odalovic.graphql.businessmodels.NewOrder
import com.odalovic.graphql.businessmodels.NewOrderItem
import com.odalovic.graphql.businessmodels.Order
import com.odalovic.graphql.businessmodels.OrderItem
import com.odalovic.graphql.db.OrderEntity
import com.odalovic.graphql.db.OrderItemEntity
import org.bson.BsonObjectId
import org.bson.types.ObjectId
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Default


@ApplicationScoped
class OrderService(@field:Default val mongoClient: MongoClient) {

    fun findById(orderId: String): Outcome<Order> {
        val objectId = try {
            ObjectId(orderId)
        } catch (e: IllegalArgumentException) {
            return Error("Invalid order ID $orderId", e)
        }
        val maybeOrder =
            ordersCollection().find(eq("_id", objectId)).projection(Projections.include("_id")).firstOrNull()
        return if (maybeOrder != null) Success(
            maybeOrder.toBusinessModel()
        ) else Error("No such order $orderId")
    }

    private fun ordersCollection() =
        mongoClient.getDatabase("order-management").getCollection("orders", OrderEntity::class.java)

    fun createOrder(newOrder: NewOrder) =
        try {
            val newOrderId = (ordersCollection().insertOne(OrderEntity(items = newOrder.items.map {
                OrderItemEntity(
                    productName = it.productName,
                    quantity = it.quantity
                )
            })).insertedId as BsonObjectId).value.toString()
            Success(newOrderId)
        } catch (e: Exception) {
            Error("Error creating new order", e)
        }

    fun deleteOrderItem(orderItemId: String): Outcome<Long> {
        return try {
            val findExistingOrderFilter = eq("items._id", orderItemId)
            val existingOrder =
                ordersCollection().find(findExistingOrderFilter).first()
                    ?: return Error("No such order containing item with ID $orderItemId")
            val totalModified = ordersCollection().updateOne(
                findExistingOrderFilter, set("items",
                    existingOrder.items.filterNot { it.id == orderItemId })
            ).modifiedCount
            Success(totalModified)
        } catch (e: Exception) {
            Error("There was an error deleting order item", e)
        }
    }

    fun deleteOrder(orderId: String) = try {
        val result = ordersCollection().deleteOne(eq("_id", ObjectId(orderId)))
        Success(result.deletedCount)
    } catch (e: Exception) {
        Error("There was an error deleting order $orderId", e)
    }

    fun upsertOrderItem(orderId: String, newOrderItem: NewOrderItem): Outcome<Order> {
        return try {
            val existingOrderFilter = eq("_id", ObjectId(orderId))
            val ordersCollection = ordersCollection()
            val order =
                ordersCollection.find(existingOrderFilter).first() ?: return Error("No such order with ID $orderId")
            var isProductAlreadyInCart = false
            val futureItems = order.items.map { itemEntity ->
                if (itemEntity.productName == newOrderItem.productName) {
                    itemEntity.quantity += newOrderItem.quantity
                    isProductAlreadyInCart = true
                    itemEntity
                } else {
                    itemEntity
                }
            }.toMutableList()
            if (!isProductAlreadyInCart) {
                futureItems += OrderItemEntity(
                    id = UUID.randomUUID().toString(),
                    productName = newOrderItem.productName,
                    quantity = newOrderItem.quantity
                )

            }
            ordersCollection.updateOne(existingOrderFilter, set("items", futureItems))
            Success(ordersCollection.find(existingOrderFilter).first()!!.toBusinessModel())
        } catch (e: Exception) {
            Error("There was an error inserting / updating order $orderId", e)
        }
    }

    fun getItems(orderId: String): Outcome<List<OrderItem>> {
        return try {
            val order =
                ordersCollection().find(eq("_id", ObjectId(orderId))).firstOrNull() ?: return Error("No such order!")
            Success(order.items.map {
                OrderItem(
                    id = it.id!!,
                    productName = it.productName,
                    quantity = it.quantity
                )
            })
        } catch (e: Exception) {
            Error("Could not get items", e)
        }
    }
}
