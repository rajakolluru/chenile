/**
 * 
 */
package org.chenile.configuration.swagger;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.chenile.swagger.init.ChenileDocketInit;
import org.chenile.swagger.model.SwaggerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Deepak N
 *
 */

@Configuration
@EnableSwagger2
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class ChenileSwaggerConfiguration implements WebMvcConfigurer {
	
	@Autowired private Environment environment;
	
	private static final Logger LOG = LoggerFactory.getLogger(ChenileSwaggerConfiguration.class);
	private static final String PAGE = "swagger-ui.html";
	private static final String PAGE_LOCATION = "classpath:/META-INF/resources/";
	private static final String WEB_JAR = "/webjars/**";
	private static final String WEB_JAR_LOCATION = "classpath:/META-INF/resources/webjars/";
	
	@Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		registry.addResourceHandler(PAGE).addResourceLocations(PAGE_LOCATION);
		registry.addResourceHandler(WEB_JAR).addResourceLocations(WEB_JAR_LOCATION);
    }
	
	@Bean
	public ChenileDocketInit chenileDocketInit() {
		return new ChenileDocketInit(swaggerMapOptions());
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void applicationStarted() throws UnknownHostException {
		final String host = InetAddress.getLocalHost().getHostAddress();
		final String port = environment.getProperty("server.port");
		
		final StringBuilder accessUrl = new StringBuilder("http://");
		accessUrl.append(host).append(":").append(port).append("/").append(PAGE);
		LOG.info("Swagger can be accessed at: " + accessUrl);
	}
	
	@Bean
	@ConfigurationProperties(prefix = "chenile.swagger.options")
	public SwaggerOptions swaggerMapOptions() {
		return new SwaggerOptions();
	}

}
