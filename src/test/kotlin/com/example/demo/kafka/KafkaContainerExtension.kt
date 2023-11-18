package com.example.demo.kafka

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"))

class KafkaContainerExtension : BeforeAllCallback, AfterAllCallback {

    override fun beforeAll(context: ExtensionContext) {
        kafka.start()
        System.setProperty("spring.kafka.bootstrap-servers", kafka.bootstrapServers)
    }

    override fun afterAll(context: ExtensionContext) {
        kafka.stop()
    }
}
