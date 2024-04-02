package org.chenile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(
		  exclude = { DataSourceAutoConfiguration.class, 
				     HibernateJpaAutoConfiguration.class,
				     DataSourceTransactionManagerAutoConfiguration.class,
				     JdbcTemplateAutoConfiguration.class})
public class TestApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}

}
