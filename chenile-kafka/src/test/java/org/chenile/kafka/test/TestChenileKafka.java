package org.chenile.kafka.test;

import org.chenile.kafka.test.service.TestConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

// @Configuration
@SpringBootApplication(scanBasePackages = {"org.chenile.configuration"})
@PropertySource("classpath:org/chenile/kafka/test/TestChenileKafka.properties")
@ActiveProfiles("unittest")
public class TestChenileKafka extends SpringBootServletInitializer{


	@Autowired ApplicationContext applicationContext;
	
	public static void main(String[] args) {
		SpringApplication.run(TestChenileKafka.class, args);
	}

		
	@Bean
	public TestConsumer testConsumer() {
		return new TestConsumer();
	}
}

