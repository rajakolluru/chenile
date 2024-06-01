package org.chenile.core.init;

import org.springframework.core.io.Resource;

/**
 * Concrete class for initializing services from Json resources
 */
public class ChenileServiceInitializer extends AbstractServiceInitializer{
	
	private final Resource[] chenileServiceJsonResources;

	public ChenileServiceInitializer(Resource[] chenileServiceJsonResources) {	
		this.chenileServiceJsonResources = chenileServiceJsonResources;
	}

	@Override
	public void init() throws Exception {
		for(Resource chenileResource: chenileServiceJsonResources ) {
			registerService(chenileResource);
		}
	}

}
