package org.chenile.core.test;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@PropertySource("classpath:org/chenile/core/test/TestChenileCore.properties")
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" })
@ActiveProfiles("unittest")
public class SpringTestConfig {
	@Value("${chenile.properties}")
	String chenileProperties;
	
	@Bean public MockService mockService() {
		return new MockService();
	}
	
	@Bean public MockService _mockMockService_() {
		return new MockMockService();
	}
	
	@Bean public MockInterceptor preProcessor1() {
		return new MockInterceptor("first","tenth");
	}
	
	@Bean public MockInterceptor preProcessor2() {
		return new MockInterceptor("second","ninth");
	}
	
	@Bean public MockInterceptor odIncludedInterceptor() {
		return new MockInterceptor("fifth","sixth");
	}
	
	@Bean public MockInterceptor postProcessor1() {
		return new MockInterceptor("third","eighth");
	}
	
	@Bean public MockInterceptor postProcessor2() {
		return new MockInterceptor("fourth","seventh");
	}
	
	@Bean public S6BodyTypeSelector s6BodyTypeSelector() {
		return new S6BodyTypeSelector();
	}
	
	@Bean public MockHealthChecker mockHealthChecker() {
		return new MockHealthChecker();
	}
	
	@Bean public T1MockService t1MockService() {
		return new T1MockService();
	}
	
}

