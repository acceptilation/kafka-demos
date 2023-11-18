package com.example.demo.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class Listener {
    private val log = LoggerFactory.getLogger(this::class.java)

    @KafkaListener(topics = ["demo-topic"])
    fun listen(record: ConsumerRecord<String, String>) {
        log.info(
            "Waiting 2 seconds before processing message '{}' with offset '{}'",
            record.key(),
            record.offset()
        )

        Thread.sleep(2000)

        log.info(
            "Finished processing message '{}' with offset '{}'",
            record.key(),
            record.offset()
        )
    }
}
