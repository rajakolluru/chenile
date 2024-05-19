package org.chenile.mqtt.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chenile.mqtt.Constants;
import org.chenile.mqtt.pubsub.MqttPublisher;
import org.chenile.mqtt.test.service.Payload;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public  class TestMqtt extends MqttBaseTest {

	@Autowired private MqttPublisher publisher;
	@Autowired private SharedData sharedData;
	@Test @Order(1) public void testIfHeadersAndPayloadWork() throws Exception {
		Payload payload = new Payload(5,8);
		Map<String, Object> headers = new HashMap<>();
		headers.put("num3",10);
		headers.put(Constants.TEST_MODE, true);
		String s = new ObjectMapper().writeValueAsString(payload);

		publisher.publishToOperation("testService","f",
				s,headers);
		if(!sharedData.latch.await(3, TimeUnit.SECONDS)){
			Assert.fail("Timed out waiting for the function to complete");
		}
		Assert.assertEquals("Sum is not computed correctly",23,sharedData.sum );
	}

}
