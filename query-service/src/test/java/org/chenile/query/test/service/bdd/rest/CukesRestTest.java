package org.chenile.query.test.service.bdd.rest;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;


@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/rest/features",
		glue = {"classpath:org/chenile/cucumber/rest", "classpath:org/chenile/query/test/service/bdd/rest"},
        plugin = {"pretty"}
        )
@ActiveProfiles("unittest")
public class CukesRestTest {

}
