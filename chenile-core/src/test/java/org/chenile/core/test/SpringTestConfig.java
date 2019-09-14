package org.chenile.core.test;


import org.chenile.core.context.EventLog;
import org.chenile.core.event.EventLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@PropertySource("classpath:org/chenile/core/test/TestChenileCore.properties")
@ComponentScan(basePackages = {"org.chenile.configuration"})
@ActiveProfiles("unittest")
public class SpringTestConfig {
	@Value("${chenile.properties}")
	String chenileProperties;
	
	@Bean EventLogger eventLogger() {
		return new EventLogger() {

			@Override
			public void log(EventLog eventLog) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
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

