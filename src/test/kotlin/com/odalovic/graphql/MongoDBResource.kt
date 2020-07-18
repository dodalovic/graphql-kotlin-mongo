package com.odalovic.graphql

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.MongoDBContainer

class MongoDBResource : QuarkusTestResourceLifecycleManager {

    private val mongoDB: MongoDBContainer = MongoDBContainer()

    override fun start(): MutableMap<String, String> {
        mongoDB.start()
        return mutableMapOf()
    }

    override fun stop() {
        mongoDB.stop()
    }
}