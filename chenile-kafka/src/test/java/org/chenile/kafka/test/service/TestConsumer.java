package org.chenile.kafka.test.service;

import org.chenile.kafka.test.TestKafkaMessaging;
import org.chenile.kafka.test.event.TestEvent;

public class TestConsumer {
	public void consume(TestEvent testEvent) {
		TestKafkaMessaging.receivedTestEvent = testEvent;
		TestKafkaMessaging.latch.countDown();
	}
}
