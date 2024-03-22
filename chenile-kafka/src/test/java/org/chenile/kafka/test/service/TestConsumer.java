package org.chenile.kafka.test.service;

import org.chenile.core.context.EventLog;
import org.chenile.kafka.test.TestKafkaMessaging;
import org.chenile.kafka.test.event.TestEvent;

public class TestConsumer {
	public EventLog consume(TestEvent testEvent) {
		TestKafkaMessaging.receivedTestEvent = testEvent;
		TestKafkaMessaging.latch.countDown();
		return null;
	}
}
