package org.chenile.mqtt.test.service;

import org.chenile.mqtt.test.SharedData;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test MQTT
 * Since MQTT is asynchronous, we need to use a countdown latch to take care of
 * co-ordinating between request and response.
 * Also, it is a fire and forget call, so we will use a common data structure to
 * validate if the service has been called and is returning correct values.
 *
 */
public class TestServiceImpl implements TestService {
	@Autowired  SharedData sharedData;
	@Override
	public int f(int num3, Payload payload) {
		int sum = payload.num1 + payload.num2 + num3;
		sharedData.sum = sum;
		sharedData.latch.countDown();
		return sum;
	}
}
