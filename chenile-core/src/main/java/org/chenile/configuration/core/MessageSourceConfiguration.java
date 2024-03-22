package org.chenile.configuration.core;

import org.chenile.core.i18n.MultipleMessageSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 
 * @author Raja Shankar Kolluru
 * Configure a message source that can consume multiple instances of the messages.properties file
 * This will allow us to modularize internationalized message handling.
 * This seems to interfere with the propertysource configurations and hence has been moved into a 
 * configuration class by itself (instead of it being in ChenileCoreConfiguration)
 */
@Configuration
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class MessageSourceConfiguration {
	
	@Value("${resource.bundle:messages}")
	private String resourceBundle;
	
	@Bean public MessageSource messageSource() {
		MultipleMessageSource mms = new MultipleMessageSource();
		mms.setBasename("classpath*:" + resourceBundle);
		return mms;
	}
	
}
