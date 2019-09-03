package org.chenile.kafka.test;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.chenile.kafka.producer.EventProducer;
import org.chenile.kafka.test.event.TestEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, topics = { "testTopic" }, controlledShutdown = true, 
			count = 1, ports = {8099} )
@SpringBootTest(classes = TestChenileKafka.class)
@ActiveProfiles("unittest")
@DirtiesContext
public class TestKafkaMessaging {
	@Autowired EmbeddedKafkaBroker embeddedKafkaBroker;
	@Autowired EventProducer eventProducer;
	
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static TestEvent receivedTestEvent;
	@Test
    public void testReceivingKafkaEvents() throws InterruptedException {
        TestEvent testEvent = new TestEvent("foo","bar"); 
        eventProducer.produceEvent(TestEvent.EVENTID, testEvent);
        // wait for the consumer to first receive the event
        latch.await(1,TimeUnit.SECONDS);
        assertEquals(testEvent,receivedTestEvent);       
    }
	 
}
