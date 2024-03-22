package org.chenile.configuration.foo;


import org.chenile.foo.interceptor.FooInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class FooConfiguration {
	
	@Bean
	public FooInterceptor fooInterceptor() {
		return new FooInterceptor();
	}
	
	
}
