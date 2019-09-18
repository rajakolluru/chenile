package org.chenile.scheduler.test;

import org.chenile.core.context.EventLog;
import org.chenile.core.event.EventLogger;
import org.chenile.scheduler.test.service.FooService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" })
@PropertySource("classpath:org/chenile/scheduler/test/TestChenileScheduler.properties")
@ActiveProfiles("unittest")
public class SpringConfig extends SpringBootServletInitializer{
	

	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}
	
	@Bean public FooService fooService() {
		return new FooService();
	}
	
	@Bean public EventLogger eventLogger() {
		return new EventLogger() {

			@Override
			public void log(EventLog eventLog) {
				
			}			
		};
	}

}

