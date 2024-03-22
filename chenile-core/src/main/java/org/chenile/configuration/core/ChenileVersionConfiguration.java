package org.chenile.configuration.core;

import org.chenile.core.model.ChenileConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * This file exists for transferring version information to ChenileConfiguration.
 * This is because we wanted two configuration files to read property files - 
 * one for version.txt and the other for chenile.properties
 * @author Raja Shankar Kolluru
 *
 */
@Configuration
@PropertySource("classpath:version.txt")
public class ChenileVersionConfiguration implements InitializingBean{
	
	@Autowired ChenileConfiguration chenileConfiguration;
	@Value("${version}")
	private String version;

	@Override
	public void afterPropertiesSet() throws Exception {
		chenileConfiguration.setVersion(version);
		
	}
}
