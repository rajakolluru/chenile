package org.chenile.workflow.service.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;


@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features",
		glue = {"classpath:org/chenile/cucumber/rest", "classpath:org/chenile/workflow/service/test"},
        plugin = {"pretty"}
        )
@ActiveProfiles("unittest")

public class CukesRestTest {

}
