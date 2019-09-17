package org.chenile.proxy.test;

import org.chenile.core.context.EventLog;
import org.chenile.core.event.EventLogger;
import org.chenile.proxy.builder.LocalProxyBuilder;
import org.chenile.proxy.test.service.FooInterceptor;
import org.chenile.proxy.test.service.FooService;
import org.chenile.proxy.test.service.FooServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" })
@PropertySource("classpath:org/chenile/proxy/test/TestChenileProxy.properties")
@ActiveProfiles("unittest")
public class SpringConfig extends SpringBootServletInitializer{
	
	@Autowired LocalProxyBuilder localProxyBuilder;

	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}
	
	@Bean @Primary public FooService fooService() {
		return localProxyBuilder.buildProxy(FooService.class, "_fooService_",null);
	}
	
	@Bean("_fooService_") public FooService _fooService_() {
		return new FooServiceImpl();
	}
	
	@Bean public FooInterceptor fooInterceptor() {
		return new FooInterceptor();
	}
	
	@Bean public EventLogger eventLogger() {
		return new EventLogger() {

			@Override
			public void log(EventLog eventLog) {
				
			}			
		};
	}

}

