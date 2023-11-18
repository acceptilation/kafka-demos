package com.example.demo.kafka

import com.ninjasquad.springmockk.SpykBean
import io.mockk.verify
import org.apache.kafka.clients.producer.ProducerRecord
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.TimeUnit.SECONDS

@SpringBootTest
@ActiveProfiles("records")
@ExtendWith(KafkaContainerExtension::class)
class MaxPollRecordsSadDemo {

    @Autowired
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    lateinit var registry: KafkaListenerEndpointRegistry

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @SpykBean
    lateinit var listener: Listener

    @Test
    fun `Never acknowledge incoming records because max poll size is too large to fit inside max poll interval`() {

        // stop the listener so that records are detected in the same poll
        registry.listenerContainers.forEach { it.stop() }

        val message1 = ProducerRecord("demo-topic", "1", "foo")
        val message2 = ProducerRecord("demo-topic", "2", "bar")
        kafkaTemplate.send(message1)
        kafkaTemplate.send(message2)
        kafkaTemplate.flush()

        // restart the listener
        registry.listenerContainers.forEach { it.start() }

        // polling interval is 3 seconds
        // polling size is 2 records
        // processing time is 2 seconds per record
        // => there's enough time in the interval to process 1 record but not 2 records
        // => listener gets stuck, processing the same 2 records over and over
        await().atMost(8, SECONDS).untilAsserted {
            verify(exactly = 4) { listener.listen(any()) }
        }
    }
}
