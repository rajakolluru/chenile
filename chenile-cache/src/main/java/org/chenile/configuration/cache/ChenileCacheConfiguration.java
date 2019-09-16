package org.chenile.configuration.cache;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class ChenileCacheConfiguration {
	@Bean	
	public HazelcastInstance hazelcastInstance(@Autowired Config config){	
		return Hazelcast.newHazelcastInstance(config);
	}
	
	@Bean
	public Config config() {
		Config cfg = new Config();
		cfg.setInstanceName("");
		MapConfig mapCfg = new MapConfig();
		mapCfg.setName("testMap");
		mapCfg.setBackupCount(2);
		mapCfg.getMaxSizeConfig().setSize(10000);
		mapCfg.setTimeToLiveSeconds(300);
		cfg.addMapConfig(mapCfg);
		return cfg;
	}
	
	
}
