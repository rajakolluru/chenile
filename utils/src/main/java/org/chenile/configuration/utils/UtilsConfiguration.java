package org.chenile.configuration.utils;

import org.chenile.utils.context.ContextContainer;
import org.chenile.utils.context.PopulateContextContainer;
import org.chenile.utils.region.RegionToTrajectoryConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfiguration {

	
	@Bean
	public org.chenile.utils.context.ContextContainer contextContainer() {
		return new ContextContainer();
	}

	@Bean 
	public PopulateContextContainer populateContextContainer() {
		return new PopulateContextContainer();
	}
	
	@Bean 
	public RegionToTrajectoryConverter regionToTrajectoryConverter() {
		return new RegionToTrajectoryConverter();
	}	
}
