package org.chenile.query.test.service.bdd.nonrest;

import org.chenile.query.test.service.SpringTestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import cucumber.api.java.en.Given;

/**
 * Dummy steps class to pull together spring configurations for Cucumber
 * @author Raja Shankar Kolluru
 *
 */
//@ContextConfiguration(classes={SpringTestConfig.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,classes = SpringTestConfig.class)
@ActiveProfiles("unittest")
public class QueryCukesSteps {
	@Given("dummy") 
	public void dummy() {}
	
}
