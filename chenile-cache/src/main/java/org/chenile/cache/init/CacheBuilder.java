package org.chenile.cache.init;

import java.util.Map;

import org.chenile.cache.model.CacheConfig;
import org.chenile.core.model.ChenileConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;

public class CacheBuilder {
	@Autowired
	private ChenileConfiguration chenileConfiguration;
	@Autowired @Qualifier("hazelcastConfig")
	private Config config;
	
	@EventListener(ApplicationReadyEvent.class)
	public void build() throws Exception {
		@SuppressWarnings("unchecked")
		Map<String,CacheConfig> map = (Map<String,CacheConfig>)
				chenileConfiguration.getOtherExtensions().get(CacheConfig.EXTENSION);
		if (map == null) return; //nothing to do
		for (CacheConfig cc : map.values()) {
			configureCache(cc);
		}
	}

	private void configureCache(CacheConfig cc) throws Exception{
		MapConfig mapCfg = new MapConfig();
		mapCfg.setName(cc.getId());
		mapCfg.setBackupCount(cc.getBackupCount());
		mapCfg.getMaxSizeConfig().setSize(cc.getMaxSize());
		mapCfg.setTimeToLiveSeconds(cc.getTimeToLiveInSeconds());
		config.addMapConfig(mapCfg);
	}
}
