package com.example.demo.kafka

import com.ninjasquad.springmockk.SpykBean
import io.mockk.verify
import org.apache.kafka.clients.producer.ProducerRecord
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.context.ActiveProfiles
import java.util.concurrent.TimeUnit.SECONDS

@SpringBootTest
@ActiveProfiles("happy")
@ExtendWith(KafkaContainerExtension::class)
class MaxPollRecordsHappyDemo {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @SpykBean
    lateinit var listener: Listener

    @Test
    fun `Acknowledge incoming records because max poll size fits inside max poll interval`() {

        val message1 = ProducerRecord("demo-topic", "1", "foo")
        val message2 = ProducerRecord("demo-topic", "2", "bar")
        kafkaTemplate.send(message1)
        kafkaTemplate.send(message2)
        kafkaTemplate.flush()

        // polling interval is 3 seconds
        // polling size is 1 record
        // processing time is 2 seconds per record
        // => there's enough time in the interval to process 1 record but not 2 records
        // => listener successfully processes each record once
        await().atMost(8, SECONDS).untilAsserted {
            verify(exactly = 2) { listener.listen(any()) }
        }
    }
}
