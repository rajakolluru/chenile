package org.chenile.workflow.service.test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import cucumber.api.java.en.Given;

/**
 * A dummy steps class to pull together the spring configuration
 * @author Raja Shankar Kolluru
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = ServiceTestConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("unittest")
public class CukesWorkflowSteps {
	@Given("Dummy")
	public void dummy() {
		
	}	
}
