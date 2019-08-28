package org.chenile.http.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ActiveProfiles;

// @Configuration
@SpringBootApplication
@PropertySource("classpath:test.properties")
@ActiveProfiles("unittest")
public class TestChenileHttp extends SpringBootServletInitializer{

	@Value("${a.parent:parent}") private String parentOfA;
	@Value("${b.parent:parent}") private String parentOfB;
	@Autowired ApplicationContext applicationContext;
	
	public static void main(String[] args) {
		SpringApplication.run(TestChenileHttp.class, args);
	}

	@Bean @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) A a() {
		Parent p = applicationContext.getBean(parentOfA,Parent.class);
		return new A(p);
	}

	@Bean B b() {
		Parent p = applicationContext.getBean(parentOfB,Parent.class);
		return new B(p);
	}
	
	@Bean Parent parent() {
		return new Parent();
	}
	
	@Bean Child child() {
		return new Child();
	}
	
		
	@Bean
	@ConfigurationProperties(prefix="a")
	public Map<String, Object> props() {
		return new HashMap<>();
	}

	
	@Bean 
	@ConfigurationProperties(prefix="liq.db")
	public LiquibaseProperties liquibaseP() {
		return new LiquibaseProperties();
	}

}

