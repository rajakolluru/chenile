package org.chenile.query.test.service.bdd.nonrest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;


@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features",
		glue = {"classpath:org.chenile.query.test.service.bdd.nonrest",
				"classpath:org/chenile/cucumber/nonrest"},
        plugin = {"pretty"}
        )
@ActiveProfiles("unittest")
public class CukesTest {

}
