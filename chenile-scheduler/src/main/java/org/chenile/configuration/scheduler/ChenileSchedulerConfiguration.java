package org.chenile.configuration.scheduler;


import org.chenile.scheduler.init.ChenileSchedulerInitializer;
import org.chenile.scheduler.init.SchedulerBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

@Configuration
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class ChenileSchedulerConfiguration {

	@Value("${chenile.scheduler.json.package:}")
	private Resource[] schedulerResources;
	@Bean public Scheduler quartzScheduler() throws SchedulerException{
		SchedulerFactory sf = new StdSchedulerFactory();
		return sf.getScheduler();
	}
	
	@Bean public SchedulerBuilder schedulerBuilder() {
		return new SchedulerBuilder();
	}

	@Bean public ChenileSchedulerInitializer chenileSchedulerInitializer(){
		return new ChenileSchedulerInitializer(schedulerResources);
	}
	
}
