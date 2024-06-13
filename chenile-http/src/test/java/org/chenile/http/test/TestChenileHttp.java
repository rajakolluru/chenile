package org.chenile.http.test;

import jakarta.annotation.PostConstruct;
import org.chenile.core.transform.SubclassRegistry;
import org.chenile.http.test.service.JsonInterceptor;
import org.chenile.http.test.service.JsonInterceptor1;
import org.chenile.http.test.service.JsonService;
import org.chenile.http.test.service.JsonServiceImpl;
import org.chenile.http.test.subclass.CapacityService;
import org.chenile.http.test.subclass.Car;
import org.chenile.http.test.subclass.Truck;
import org.chenile.http.test.subclass.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@SpringBootApplication(scanBasePackages = { "org.chenile.configuration" , "org.chenile.http.test.controller"})
@PropertySource("classpath:org/chenile/http/test/TestHttpModule.properties")
@ActiveProfiles("unittest")
public class TestChenileHttp extends SpringBootServletInitializer{
	@Autowired SubclassRegistry subclassRegistry;
	public static void main(String[] args) {
		SpringApplication.run(TestChenileHttp.class, args);
	}
	
	@Bean public JsonService jsonService() {
		return new JsonServiceImpl();
	}
	
	@Bean public JsonInterceptor jsonInterceptor() {
		return new JsonInterceptor();
	}
	@Bean public JsonInterceptor1 jsonInterceptor1() {
		return new JsonInterceptor1();
	}
	
	@Bean public TestUtil testUtil() {
		return new TestUtil();
	}
	@Bean public CapacityService capacityService() {
		return new CapacityService();
	}

	@PostConstruct public void postConstruct(){
		subclassRegistry.addSubclass(Vehicle.class,"car", Car.class);
		subclassRegistry.addSubclass(Vehicle.class,"truck", Truck.class);
	}
}

