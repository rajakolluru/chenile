package org.chenile.configuration.cache;


import org.chenile.cache.interceptor.CacheInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
@PropertySource("classpath:${chenile.properties:chenile.properties}")
public class ChenileCacheConfiguration {
	
	@Value("${chenile.cache.name}")
	private String cacheName;
	
	@Bean	
	public HazelcastInstance hazelcastInstance(@Autowired @Qualifier("hazelcastConfig") Config config){	
		return Hazelcast.newHazelcastInstance(config);
	}
	
	@Bean
	public Config hazelcastConfig() {
		Config cfg = new Config();
		cfg.setInstanceName(cacheName);
		return cfg;
	}
	
	@Bean
	public CacheInterceptor chenileCachingInterceptor() {
		return new CacheInterceptor();
	}
	
	
}
