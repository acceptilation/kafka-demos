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
@ActiveProfiles("interval")
@ExtendWith(KafkaContainerExtension::class)
class MaxPollIntervalSadDemo {

    @Autowired
    lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @SpykBean
    lateinit var listener: Listener

    @Test
    fun `Never acknowledge incoming record because max poll interval is less than processing time`() {
        kafkaTemplate.send(ProducerRecord("demo-topic", "1", "foo"))
        kafkaTemplate.flush()

        // polling interval is 1 second
        // processing time is 2 seconds
        // => listener gets stuck, processing the same record over and over
        await().atMost(7, SECONDS).untilAsserted {
            verify(exactly = 3) { listener.listen(any()) }
        }
    }
}
