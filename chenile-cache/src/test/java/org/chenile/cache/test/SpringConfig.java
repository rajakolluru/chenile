package org.chenile.cache.test;

import org.chenile.cache.test.service.FooService;
import org.chenile.cache.test.service.FooServiceImpl;
import org.chenile.proxy.builder.ProxyBuilder;
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
@PropertySource("classpath:org/chenile/cache/test/TestChenileCache.properties")
@ActiveProfiles("unittest")
public class SpringConfig extends SpringBootServletInitializer{
	
	@Autowired ProxyBuilder proxyBuilder;
	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}
	
	@Bean("_fooService_") public FooService _fooService_() {
		return new FooServiceImpl();
	}
	
	@Bean @Primary public FooService fooService() {
		return proxyBuilder.buildProxy(FooService.class,"fooService", null,
				"localhost:8080");
	}
}

