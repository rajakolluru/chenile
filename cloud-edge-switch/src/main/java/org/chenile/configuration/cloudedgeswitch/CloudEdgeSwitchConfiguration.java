package org.chenile.configuration.cloudedgeswitch;


import org.chenile.cloudedgeswitch.interceptor.CloudEdgeSwitch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class CloudEdgeSwitchConfiguration {
	@Bean @ConfigurationProperties(prefix = "cloud")
	public CloudEdgeSwitch cloudEdgeSwitch() {
		return new CloudEdgeSwitch();
	}
}
