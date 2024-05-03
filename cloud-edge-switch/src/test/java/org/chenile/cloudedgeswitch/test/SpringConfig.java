package org.chenile.cloudedgeswitch.test;

import org.chenile.cloudedgeswitch.test.service.TestService;
import org.chenile.cloudedgeswitch.test.service.TestServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@SpringBootApplication(scanBasePackages = {  "org.chenile.configuration",
			"org.chenile.cloudedgeswitch.test.service"})
@PropertySource("classpath:org/chenile/cloudedgeswitch/test/TestCloudEdgeSwitch.properties")
@ActiveProfiles("unittest")
public class SpringConfig extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}
	
	@Bean("testService") public TestService testService() {
		return new TestServiceImpl();
	}
}

