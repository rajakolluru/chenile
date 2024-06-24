package org.chenile.security.test;

import org.chenile.core.context.ChenileExchange;
import org.chenile.security.AuthoritiesSupplier;
import org.chenile.security.test.service.TestService;
import org.chenile.security.test.service.TestServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

import java.util.function.Function;

@Configuration
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration", "org.chenile.security.test.service"})
@PropertySource("classpath:org/chenile/security/test/TestSecurity.properties")
@ActiveProfiles("unittest")
public class SpringConfig extends SpringBootServletInitializer{
	
	public static void main(String[] args) {
		SpringApplication.run(SpringConfig.class, args);
	}
	
	@Bean("testService") public TestService testService() {
		return new TestServiceImpl();
	}

	@Bean public Function<ChenileExchange,String[]> supplier(AuthoritiesSupplier as){
		return as::getAuthorities;
	}

	@Bean public AuthoritiesSupplier authoritiesSupplier(){
		return new AuthoritiesSupplier(){

			@Override
			public String[] getAuthorities(ChenileExchange exchange) {
				String option = exchange.getHeader("option",String.class);
				if (option.equals("foo")) {
					return null;
				}
				return new String[]{"order.write"};
			}
		};
	}
}

