package com.odalovic.graphql.db

import com.mongodb.BasicDBObject
import com.mongodb.MongoClientSettings
import org.bson.BsonReader
import org.bson.BsonString
import org.bson.BsonWriter
import org.bson.Document
import org.bson.codecs.Codec
import org.bson.codecs.CollectibleCodec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext
import org.bson.types.ObjectId
import java.util.*

class OrderCodec : CollectibleCodec<OrderEntity> {

    private val documentCodec: Codec<Document> = MongoClientSettings.getDefaultCodecRegistry().get(Document::class.java)

    override fun getEncoderClass() = OrderEntity::class.java

    override fun generateIdIfAbsentFromDocument(document: OrderEntity) = document

    override fun encode(writer: BsonWriter, value: OrderEntity, encoderContext: EncoderContext) {
        val doc = Document().apply {
            put("items", value.items.map {
                BasicDBObject(
                    mapOf(
                        "_id" to ObjectId(),
                        "productName" to it.productName,
                        "quantity" to it.quantity
                    )
                )
            }
            )
        }
        documentCodec.encode(writer, doc, encoderContext)
    }

    override fun documentHasId(document: OrderEntity) = true

    override fun decode(reader: BsonReader, decoderContext: DecoderContext): OrderEntity {
        val rawDoc = documentCodec.decode(reader, decoderContext)
        val itemsRaw = rawDoc["items"] as List<Document>
        return OrderEntity(rawDoc.getObjectId("_id").toString(), itemsRaw.map {
            OrderItemEntity(
                id = it.getObjectId("_id").toString(),
                productName = it.getString("productName"),
                quantity = it.getInteger("quantity")
            )
        })
    }

    override fun getDocumentId(document: OrderEntity) = BsonString(UUID.randomUUID().toString())
}