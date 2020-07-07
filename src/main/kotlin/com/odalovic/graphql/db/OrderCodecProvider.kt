package com.odalovic.graphql.db

import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecProvider
import org.bson.codecs.configuration.CodecRegistry

class OrderCodecProvider : CodecProvider {
    override fun <T : Any?> get(clazz: Class<T>, registry: CodecRegistry): Codec<T>? {
        if (clazz == OrderEntity::class.java) {
            return OrderCodec() as Codec<T>
        }
        return null
    }
}