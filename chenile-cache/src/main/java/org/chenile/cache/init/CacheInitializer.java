package org.chenile.cache.init;

import java.util.Map;

import org.chenile.cache.model.CacheConfig;
import org.chenile.core.init.BaseInitializer;
import org.springframework.core.io.Resource;

public class CacheInitializer extends BaseInitializer<CacheConfig>{

	public CacheInitializer(Resource[] resources) {
		super(resources);
	}

	@Override
	protected void registerModelInChenile(CacheConfig cacheConfig) {
		Map<String,CacheConfig> map = getExtensionMap(CacheConfig.EXTENSION);
		map.put(cacheConfig.getId(), cacheConfig);	
	}

	@Override
	protected Class<CacheConfig> getModelType() {
		return CacheConfig.class;
	}

}
