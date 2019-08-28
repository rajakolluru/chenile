package org.chenile.core.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ComponentScan(basePackages = {"org.chenile.configuration"})
@PropertySource("classpath:org/chenile/core/test/TestChenileCore.properties")
@ActiveProfiles("unittest")
public class SpringTestConfig {
	@Bean public MockService mockService() {
		return new MockService();
	}
	
	@Bean public MockInterceptor preProcessor1() {
		return new MockInterceptor("first","tenth");
	}
	
	@Bean public MockInterceptor preProcessor2() {
		return new MockInterceptor("second","ninth");
	}
	
	@Bean public MockInterceptor odIncludedInterceptor() {
		return new MockInterceptor("third","eighth");
	}
	
	@Bean public MockInterceptor postProcessor1() {
		return new MockInterceptor("fourth","seventh");
	}
	
	@Bean public MockInterceptor postProcessor2() {
		return new MockInterceptor("fifth","sixth");
	}
	
	@Bean public S6BodyTypeSelector s6BodyTypeSelector() {
		return new S6BodyTypeSelector();
	}
	
}

