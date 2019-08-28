package org.chenile.configuration.http;


import org.chenile.http.init.HttpModuleBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

@Configuration
@EnableWebMvc
public class UrlMappingConfiguration {
	
    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping(){
        return new HttpModuleBuilder();
    }	
}
