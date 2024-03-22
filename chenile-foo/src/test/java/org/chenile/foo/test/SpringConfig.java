package org.chenile.foo.test;

import org.chenile.core.annotation.ConditionalHealthCheckOnTrajectory;
import org.chenile.core.annotation.ConditionalOnTrajectory;
import org.chenile.core.service.HealthChecker;
import org.chenile.foo.test.service.TestHealthChecker;
import org.chenile.foo.test.service.TestHealthChecker1;
import org.chenile.foo.test.service.TestService;
import org.chenile.foo.test.service.TestServiceImpl;
import org.chenile.foo.test.service.TestServiceImpl1;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" , "org.chenile.foo.test.service"})
@PropertySource("classpath:org/chenile/foo/test/TestFoo.properties")
@ActiveProfiles("unittest")
public class SpringConfig extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}
	
	@Bean("testService") public TestService testService() {
		return new TestServiceImpl();
	}
	
	@Bean("testHealthChecker") public HealthChecker testHealthChecker() {
		return new TestHealthChecker();
	}
	
	@Bean("testService1") 
	@ConditionalOnTrajectory(id = "xxx",service = "testService") 
	public TestService testService1() {
		return new TestServiceImpl1();
	}
	
	@Bean("testHealthChecker1") 
	@ConditionalHealthCheckOnTrajectory(id = "xxx",service = "testService") 
	public TestHealthChecker1 testHealthChecker1() {
		return new TestHealthChecker1();
	}
}

