package org.chenile.http.test;

import org.chenile.http.test.service.JsonService;
import org.chenile.http.test.service.JsonServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" })
@PropertySource("classpath:org/chenile/http/test/TestHttpModule.properties")
@ActiveProfiles("unittest")
public class TestChenileHttp extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(TestChenileHttp.class, args);
	}
	
	@Bean public JsonService jsonService() {
		return new JsonServiceImpl();
	}

}

