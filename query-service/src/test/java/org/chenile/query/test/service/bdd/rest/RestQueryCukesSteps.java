package org.chenile.query.test.service.bdd.rest;

import org.chenile.query.test.service.SpringTestConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import cucumber.api.java.en.Given;

/**
 * 
 * The reference to SpringBootTest is important 
 * @author Raja Shankar Kolluru
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = SpringTestConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("unittest")
public class RestQueryCukesSteps {
	// Create a dummy method so that Cucumber thinks of this as a steps implementation.
	@Given("dummy")
	public void dummy() {
		
	}
	
}
