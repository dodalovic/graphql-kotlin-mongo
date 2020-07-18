package com.odalovic.graphql

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager
import org.testcontainers.containers.GenericContainer

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

class MongoDBResource : QuarkusTestResourceLifecycleManager {

    companion object {
        var mongoDB: KGenericContainer
        private val mongoEnv = mutableMapOf(
            "MONGO_INITDB_ROOT_USERNAME" to "root",
            "MONGO_INITDB_ROOT_PASSWORD" to "example",
            "MONGO_INITDB_DATABASE" to "order-management"
        )

        init {
            mongoDB = KGenericContainer("mongo:4.2").withEnv(mongoEnv).withExposedPorts(27017)
        }
    }

    override fun start(): MutableMap<String, String> {
        mongoDB.start()
        return mutableMapOf(
            "quarkus.mongodb.connection-string" to "mongodb://${mongoDB.containerIpAddress}:${mongoDB.firstMappedPort}"
        )
    }

    override fun stop() {
        mongoDB.stop()
    }
}