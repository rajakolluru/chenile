package org.chenile.proxy.test;

import org.chenile.proxy.builder.ProxyBuilder;
import org.chenile.proxy.builder.ProxyBuilder.ProxyMode;
import org.chenile.proxy.test.service.FooInterceptor;
import org.chenile.proxy.test.service.FooService;
import org.chenile.proxy.test.service.FooServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@PropertySource("classpath:application-fixedport.properties")
@ActiveProfiles("unittest")
public class SpringConfig extends SpringBootServletInitializer{
	
	@Autowired ProxyBuilder proxyBuilder;
	@Value("${server.port}") String serverPort;


	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}
	
	@Bean @Primary public FooService fooService() {
		return proxyBuilder.buildProxy(FooService.class, "fooService",null,
				"localhost:" + serverPort);
	}
	
	@Bean("_fooService_") public FooService _fooService_() {
		return new FooServiceImpl();
	}
	
	@Bean public FooInterceptor fooInterceptor() {
		return new FooInterceptor();
	}
	
	@Bean public FooService fooServiceOnlyRemote() {
		return proxyBuilder.buildProxy(FooService.class, "fooService",null,
				ProxyMode.REMOTE, "localhost:" + serverPort);
	}

	@Bean public FooService wireMockProxy() {
		return proxyBuilder.buildProxy(FooService.class, "fooService",null,
				ProxyMode.REMOTE, "localhost:8089");
		// 8089 is the wire mock port instantiated in TestChenileProxy
	}
}

