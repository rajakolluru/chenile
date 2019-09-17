package org.chenile.configuration.proxy;

import org.chenile.proxy.builder.ProxyBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfiguration {
	@Bean public ProxyBuilder localProxyBuilder() {
		return new ProxyBuilder();
	}
}
