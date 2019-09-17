package org.chenile.configuration.proxy;

import org.chenile.proxy.builder.LocalProxyBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfiguration {
	@Bean public LocalProxyBuilder localProxyBuilder() {
		return new LocalProxyBuilder();
	}
}
