package org.chenile.cloudedgeswitch.test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.chenile.mqtt.test.MqttBaseTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringConfig.class)
@ActiveProfiles("unittest")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public  class TestCloudEdgeSwitch extends MqttBaseTest {
	// 8089 must be the same port as defined in the application.properties file
	@Rule public WireMockRule wireMockRule = new WireMockRule(8089);
	@Autowired private MockMvc mvc;
	@Test @Order(1) public void t1() throws Exception {
		mvc.perform( MockMvcRequestBuilders
            .get("/f1")
            .accept(MediaType.APPLICATION_JSON))
        	.andDo(print())
        	.andExpect(status().isOk())
        	.andExpect(jsonPath("$.payload.test").value("wire-mock-test"));
	}

	@Test @Order(2) public void t2() throws Exception {
		String json = "{ \"a\": \"abc\", \"b\":\"def\"}";
		mvc.perform( MockMvcRequestBuilders
						.post("/f2/ghi")
						.content(json)
						.contentType("application/json")
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				// .andExpect(status().isOk())
				.andExpect(jsonPath("$.payload.a").value("abc-wire-mock"));
	}

	@Test @Order(3) public void t3() throws Exception {
		wireMockRule.stop();
		mvc.perform( MockMvcRequestBuilders
				.get("/f1")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.payload.test").value("test"));
	}
	@Test @Order(4) public void t4() throws Exception {
		String json = "{ \"a\": \"abc\", \"b\":\"def\"}";
		wireMockRule.stop();
		mvc.perform( MockMvcRequestBuilders
						.post("/f2/ghi")
						.content(json)
						.contentType("application/json")
						.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isAccepted())
				.andExpect(jsonPath("$.payload.a").value("abc"));
	}
}
